
export class AppUtils {

    public static formatTimestamp(value: string) {
        const idx_to = value.indexOf(".");
        if(idx_to >= 0) {
            return value.substr(0, idx_to).replace("T", " ");
        } else {
            return value.replace("T", " ");
        }
    }
}
