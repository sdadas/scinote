package com.sdadas.scinote.repos.url;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.net.InternetDomainName;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.sdadas.scinote.repos.shared.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sławomir Dadas
 */
public class UrlParser {

    private final static String DOI_REGEX = "\\b(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&'<>])\\S)+)\\b";

    private final static Pattern DOI_PATTERN = Pattern.compile(DOI_REGEX, Pattern.UNICODE_CASE);

    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d{4}");

    private final Url url;

    private Document document;

    public UrlParser(Url url) {
        this.url = url;
    }

    public void download() throws IOException {
        SSLSocketFactory sf = socketFactory();
        Connection.Response resp = Jsoup.connect(url.getOriginalUrl())
                .sslSocketFactory(sf)
                .maxBodySize(10 * 1024 * 1024)
                .timeout(10000)
                .execute();
        int status = resp.statusCode();
        if(status != HttpStatus.OK.value()) {
            throw new IOException("Invalid http status " + status);
        }
        this.document = resp.parse();
    }

    @SuppressWarnings("UnstableApiUsage")
    public List<String> findDOI() {
        String html = document.outerHtml();
        UrlDetector detector = new UrlDetector(html, UrlDetectorOptions.HTML);
        List<Url> detected = detector.detect();
        if(detected.isEmpty()) return Collections.emptyList();
        List<String> results = new ArrayList<>();
        for (Url detectedUrl : detected) {
            String host = detectedUrl.getHost();
            try {
                InternetDomainName domain = InternetDomainName.from(host);
                if(domain.isUnderRegistrySuffix()) {
                    String domainName = domain.topDomainUnderRegistrySuffix().toString();
                    if(StringUtils.equalsIgnoreCase(domainName, "doi.org")) {
                        results.add(detectedUrl.getOriginalUrl());
                    }
                }
            } catch (IllegalArgumentException ex) {
                /* IGNORE AND CONTINUE */
            }
        }
        if(results.isEmpty()) {
            Matcher matcher = DOI_PATTERN.matcher(html);
            while(matcher.find()) {
                results.add(matcher.group());
            }
        }
        return results;
    }

    public Paper parse() {
        MultiValueMap<String, String> meta = getMetaProperties();
        String metaUrl = firstNotBlankKey(meta, "citation_public_url", "og:url");
        String title = firstNotBlankKey(meta, "citation_title", "DC.Title", "og:title");
        Paper paper = new Paper();
        if(StringUtils.isNotBlank(metaUrl) && !StringUtils.equals(metaUrl, url.getOriginalUrl())) {
            paper.addId(new PaperId("url", metaUrl));
        }
        paper.addId(new PaperId("url", url.getOriginalUrl()));
        paper.setTitle(firstNotBlank(title, document.title()));
        paper.setType(PaperType.MISC);
        paper.setKeywords(getKeywords(meta));
        paper.setSource(createSource(meta));
        paper.setUrls(createUrls(meta));
        paper.setSummary(firstNotBlankKey(meta, "citation_abstract", "og:description", "description"));
        paper.setPages(createPages(meta));
        paper.setYear(createYear(meta));
        paper.setAuthors(createAuthors(meta));
        String doi = firstNotBlankKey(meta, "citation_doi");
        if(StringUtils.isNotBlank(doi)) {
            paper.setDoi(doi);
            paper.addId(new PaperId("doi", doi));
        }
        Source source = paper.getSource();
        if(StringUtils.isNotBlank(source.getName())) {
            paper.setType(PaperType.ARTICLE);
        } else {
            paper.getSource().setName(getDomain());
        }
        return paper;
    }

    @SuppressWarnings("UnstableApiUsage")
    private String getDomain() {
        String host = url.getHost();
        try {
            InternetDomainName domain = InternetDomainName.from(host).topDomainUnderRegistrySuffix();
            return domain.toString();
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private List<Author> createAuthors(MultiValueMap<String, String> meta) {
        List<String> authors = meta.get("citation_author");
        if(authors == null || authors.isEmpty()) authors = meta.get("author");
        List<Author> results = new ArrayList<>();
        if(authors == null) return results;
        int idx = 0;
        for (String author : authors) {
            author = StringUtils.strip(author);
            if(StringUtils.isBlank(author)) continue;
            if(StringUtils.contains(author, ',')) {
                String lastName = StringUtils.substringBefore(author, ",");
                String firstName = StringUtils.substringAfter(author, ",").trim();
                results.add(new Author(firstName, lastName, idx));
            } else {
                String firstName = StringUtils.substringBeforeLast(author, " ");
                String lastName = StringUtils.substringAfterLast(author, " ");
                results.add(new Author(firstName, lastName, idx));
            }
            idx++;
        }
        return results;
    }

    private String createPages(MultiValueMap<String, String> meta) {
        String firstPage = meta.getFirst("citation_firstpage");
        String lastPage = meta.getFirst("citation_lastpage");
        if(StringUtils.isNotBlank(firstPage) && StringUtils.isNotBlank(lastPage)) {
            return String.format("%s-%s", firstPage, lastPage);
        } else if(StringUtils.isNotBlank(firstPage)) {
            return firstPage;
        } else if(StringUtils.isNotBlank(lastPage)) {
            return lastPage;
        } else {
            return null;
        }
    }

    private Integer createYear(MultiValueMap<String, String> meta) {
        String publicationDate = meta.getFirst("citation_publication_date");
        int now = LocalDate.now().getYear();
        if(StringUtils.isNotBlank(publicationDate)) {
            Matcher matcher = YEAR_PATTERN.matcher(publicationDate);
            while(matcher.find()) {
                int value = Integer.parseInt(matcher.group());
                if(value > 1800 && value <= (now + 1)) {
                    return value;
                }
            }
        }
        return null;
    }

    private List<WebLocation> createUrls(MultiValueMap<String, String> meta) {
        String metaUrl = firstNotBlankKey(meta, "citation_public_url", "og:url");
        List<WebLocation> results = new ArrayList<>();
        results.add(new WebLocation("html", firstNotBlank(metaUrl, url.getOriginalUrl())));
        String pdfUrl = firstNotBlankKey(meta, "citation_pdf_url");
        if (StringUtils.isNotBlank(pdfUrl)) {
            results.add(new WebLocation("pdf", pdfUrl));
        }
        return results;
    }

    private Source createSource(MultiValueMap<String, String> meta) {
        Source source = new Source();
        source.setName(firstNotBlankKey(meta, "citation_journal_title", "citation_conference_title"));
        source.setPublisher(firstNotBlankKey(meta, "citation_publisher", "DC.Publisher", "publisher"));
        source.setVenue(firstNotBlankKey(meta, "citation_conference_title"));
        return source;
    }

    private List<String> getKeywords(MultiValueMap<String, String> meta) {
        String keywords = meta.getFirst("keywords");
        List<String> results = new ArrayList<>();
        if(keywords == null) return results;
        Iterable<String> split = Splitter.on(CharMatcher.anyOf(";,")).trimResults().split(keywords);
        split.forEach(results::add);
        return results;
    }

    private String firstNotBlankKey(MultiValueMap<String, String> meta, String... keys) {
        for (String key : keys) {
            String value = meta.getFirst(key);
            if(StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if(StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private MultiValueMap<String, String> getMetaProperties() {
        Elements meta = document.getElementsByTag("meta");
        MultiValueMap<String, String> res = new LinkedMultiValueMap<>();
        for (Element element : meta) {
            String name = ObjectUtils.firstNonNull(element.attr("name"), element.attr("property"));
            String value = element.attr("content");
            if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                res.add(name, value);
            }
        }
        return res;
    }

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}
