package xyz.kymirai.translator.utills;

public enum Language {
    auto(0, "auto", "未知"),
    zh_CHS(1, "zh-CHS", "中"),
    en(2, "en", "英"),
    ja(3, "ja", "日");
    //unknow(-1, "auto", "未知");

    public int type;
    public String value;
    public String text;

    Language(int type, String value, String text) {
        this.value = value;
        this.type = type;
        this.text = text;
    }

    public static Language getLanguage(int type) {
        switch (type) {
            case 0:
                return auto;
            case 1:
                return zh_CHS;
            case 2:
                return en;
            case 3:
                return ja;
            default:
                return auto;
        }
    }

    public static Language getLanguage(String text) {
        switch (text) {
//            case "auto":
//            case "未知":
//                return auto;
            case "zh-CHS":
            case "中":
                return zh_CHS;
            case "en":
            case "En":
            case "EN":
            case "英":
                return en;
            case "ja":
            case "JA":
            case "Ja":
            case "日":
                return ja;
            default:
                return auto;
        }
    }

    public static String encode(int from, int to) {
        return Language.getLanguage(from).value + "2" + Language.getLanguage(to).value;
    }

    public static String encode(String l) {
        String[] strs;
        if ((strs = l.split("2")).length == 2) {
            return Language.getLanguage(strs[0]).value + "2" + Language.getLanguage(strs[1]).value;
        } else {
            return "auto2auto";
        }

    }

    public static String decode(String str) {
        try {
            String[] strs = str.split("2");
            return Language.getLanguage(strs[0]).text + " -> " + Language.getLanguage(strs[1]).text;
        } catch (Exception e) {
            e.printStackTrace();
            return "未知 -> 未知";
        }
    }

}
