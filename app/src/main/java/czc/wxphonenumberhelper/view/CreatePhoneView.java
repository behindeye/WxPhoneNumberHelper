package czc.wxphonenumberhelper.view;

import java.util.List;

import czc.wxphonenumberhelper.base.BaseView;

/**
 * @author zhicheng.chen
 * @date 2018/8/2
 */
public interface CreatePhoneView extends BaseView {
	void showProgressDialog();
	void hideProgressDialog();
	void showResult(List<String> data);
	void success();
}
