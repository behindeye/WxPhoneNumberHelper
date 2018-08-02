package czc.wxphonenumberhelper.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author zhicheng.chen
 * @date 2018/8/2
 */
@Entity
public class WechatFriendRecord {

	@Id
	private String name;
	private String account;
	private String phoneNumber;
	private String area;
	private String distance;
	private int sex;
	private boolean hasAdd;
	private String iconPath;
	@Generated(hash = 669256371)
	public WechatFriendRecord(String name, String account, String phoneNumber,
			String area, String distance, int sex, boolean hasAdd, String iconPath) {
		this.name = name;
		this.account = account;
		this.phoneNumber = phoneNumber;
		this.area = area;
		this.distance = distance;
		this.sex = sex;
		this.hasAdd = hasAdd;
		this.iconPath = iconPath;
	}
	@Generated(hash = 1062229915)
	public WechatFriendRecord() {
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccount() {
		return this.account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getArea() {
		return this.area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDistance() {
		return this.distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public int getSex() {
		return this.sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public boolean getHasAdd() {
		return this.hasAdd;
	}
	public void setHasAdd(boolean hasAdd) {
		this.hasAdd = hasAdd;
	}
	public String getIconPath() {
		return this.iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
}
