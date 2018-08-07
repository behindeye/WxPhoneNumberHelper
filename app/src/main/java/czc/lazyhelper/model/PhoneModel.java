package czc.lazyhelper.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by czc on 2018/8/1.
 */

@Entity
public class PhoneModel {
    @Id
    private String number;
    private String name;
    @Generated(hash = 1718099963)
    public PhoneModel(String number, String name) {
        this.number = number;
        this.name = name;
    }
    @Generated(hash = 2003199864)
    public PhoneModel() {
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
