
export class AppUtils {

    public static formatTimestamp(value: string) {
        const idx_to = value.indexOf(".");
        if(idx_to >= 0) {
            return value.substr(0, idx_to).replace("T", " ");
        } else {
            return value.replace("T", " ");
        }
    }

    public static abbr(value: string, chars: number) {
        if(!value) return value;
        if(value.length > chars && chars > 3) {
            return value.substr(0, chars - 3) + "..."
        } else {
            return value;
        }
    }
}
