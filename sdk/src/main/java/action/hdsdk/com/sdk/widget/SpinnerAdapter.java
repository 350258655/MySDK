package action.hdsdk.com.sdk.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

import action.hdsdk.com.sdk.R;
import action.hdsdk.com.sdk.db.UserList;

/**
 * Created by shake on 17-6-3.
 */
public class SpinnerAdapter extends BaseAdapter {

    private List<String> mDatas;
    private Context mContext;

    public SpinnerAdapter(Context context,List<String> datas){
        this.mContext = context;
        this.mDatas = datas;
    }


    @Override
    public int getCount() {
        if(mDatas != null){
            return mDatas.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mDatas != null){
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            //加载布局
            convertView = View.inflate(mContext, R.layout.item, null);

            //初始化Holder
            holder = new ViewHolder();
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);

            //设置标记
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 给View设置数据
        holder.tvTitle.setText(mDatas.get(position));
        // 设置删除监听事件
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除数据
                mDatas.remove(position);

                // 而且得删除Map中的数据
                UserList.removeUser(mDatas.get(position),mContext);

                // 更新数据
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    class ViewHolder{
        ImageView ivDelete;
        TextView tvTitle;
    }
}
