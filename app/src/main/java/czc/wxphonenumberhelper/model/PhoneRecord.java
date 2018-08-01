package czc.wxphonenumberhelper.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by czc on 2018/8/1.
 */

@Entity
public class PhoneRecord {
    @Id
    private String number;
    private String name;
    @Generated(hash = 243448601)
    public PhoneRecord(String number, String name) {
        this.number = number;
        this.name = name;
    }
    @Generated(hash = 314307933)
    public PhoneRecord() {
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
