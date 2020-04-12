package com.sdadas.scinote.shared.model.project;

import com.sdadas.scinote.shared.model.paper.PaperId;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sławomir Dadas
 */
public class Candidates implements Serializable {

    private Set<PaperId> singles = new HashSet<>();

    private Map<PaperId, Integer> multiples = new HashMap<>();

    public Set<PaperId> getSingles() {
        return singles;
    }

    public void setSingles(Set<PaperId> singles) {
        this.singles = singles;
    }

    public Map<PaperId, Integer> getMultiples() {
        return multiples;
    }

    public void setMultiples(Map<PaperId, Integer> multiples) {
        this.multiples = multiples;
    }

    void increment(PaperId id) {
        if(this.singles.contains(id)) {
            this.singles.remove(id);
            this.multiples.put(id, 1);
        } else {
            Integer count = this.multiples.get(id);
            if(count != null) {
                this.multiples.put(id, count + 1);
            } else {
                this.singles.add(id);
            }
        }
    }

    void decrement(PaperId id) {
        if(this.singles.contains(id)) {
            this.singles.remove(id);
        } else {
            Integer count = this.multiples.get(id);
            if(count != null) {
                count = count - 1;
                this.multiples.put(id, count);
                if(count == 1) this.singles.add(id);
                if(count <= 1) this.multiples.remove(id);
            }
        }
    }

    void remove(PaperId id) {
        this.singles.remove(id);
        this.multiples.remove(id);
    }

    public List<PaperId> topN(int n) {
        return multiples.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(n).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public int size() {
        return this.singles.size() + this.multiples.size();
    }
}
