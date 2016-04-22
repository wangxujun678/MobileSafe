package cn.example.mobilesafe.bean;

/**
 * 黑名单信息，包括号码number和拦截模式mode
 * 1 全部拦截 电话拦截 + 短信拦截
 * 2 电话拦截
 * 3 短信拦截
 */
public class BlackNumberInfo {

    private String number;
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
