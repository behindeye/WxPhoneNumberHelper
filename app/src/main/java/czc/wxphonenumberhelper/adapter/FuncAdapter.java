package czc.wxphonenumberhelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import czc.wxphonenumberhelper.R;
import czc.wxphonenumberhelper.activity.ValidateActivity;
import czc.wxphonenumberhelper.model.FuncItem;
import czc.wxphonenumberhelper.ui.DialogView;
import czc.wxphonenumberhelper.ui.OnDialogClickListener;
import czc.wxphonenumberhelper.util.WxAppUtil;

/**
 * Created by czc on 2017/7/7.
 */

public class FuncAdapter extends RecyclerView.Adapter<FuncAdapter.FuncHolder> {

    Context mContext;
    List<FuncItem> mFuncItemList;
    private final DialogView dv;

    public FuncAdapter(Context context, List<FuncItem> funcItemList) {
        mContext = context;
        mFuncItemList = funcItemList;
        dv = new DialogView(mContext);
        dv.setMessage("您需要激活软件，才能使用这个功能~");
        dv.setCancel(null);
        dv.setOk("确定");
        dv.AddDialogClickListener(new OnDialogClickListener() {
            @Override
            public void OnOKClickListener(View v) {
                mContext.startActivity(new Intent(mContext, ValidateActivity.class));
                dv.dismiss();
            }

            @Override
            public void OnCancelClickListener(View v) {

            }

            @Override
            public void OnCustomClickListener(View v) {

            }
        });
    }

    @Override
    public FuncHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final FuncHolder holder = new FuncHolder(LayoutInflater.from(mContext).inflate(R.layout.item_func, parent, false));
        if (mOnItemClickLitener != null) {
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (mOnItemClickLitener != null) {
                        if (mFuncItemList.get(pos).isNeedValidate) {
                            if (WxAppUtil.isValiditySoftWare()) {
                                mOnItemClickLitener.onItemClick(pos);
                            } else {
                                dv.show();
                            }
                        } else {
                            mOnItemClickLitener.onItemClick(pos);
                        }
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(FuncHolder holder, int position) {
        FuncItem item = mFuncItemList.get(position);
        holder.icon.setImageDrawable(mContext.getResources().getDrawable(item.resId));
        holder.funcName.setText(item.funcName);
        holder.subFuncName.setText(item.subFuncName);
    }

    @Override
    public int getItemCount() {
        return mFuncItemList.size();
    }

    class FuncHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView icon;
        @BindView(R.id.tv_func_name)
        TextView funcName;
        @BindView(R.id.tv_sub_func_name)
        TextView subFuncName;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        public FuncHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener mOnItemClickLitener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
