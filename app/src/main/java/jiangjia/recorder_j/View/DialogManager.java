package jiangjia.recorder_j.View;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jiangjia.recorder_j.R;

/**
 * Created by KelceiJ on 2017/3/7.
 */

public class DialogManager {
    private Dialog mDialog;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLabel;
    private Context mContext;

    public DialogManager(Context context)
    {
        this.mContext=context;
    }

    public void showRecordingDialog(){
        mDialog=new Dialog(mContext, R.style.Theme_AudioDialog);
        //LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater= LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);

        mIcon=(ImageView)mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mVoice=(ImageView)mDialog.findViewById((R.id.id_recorder_dialog_voice));
        mLabel=(TextView)mDialog.findViewById(R.id.id_recorder_dialog_label);

        mDialog.show();
    }

    public void recording(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.str_label_up);
        }
    }

    public void wantToCancel(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.str_label_release);
        }
    }

    public void tooShort(){
        if(mDialog!=null&&mDialog.isShowing()){

            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText(R.string.str_label_short);
        }
    }

    public void dismissDialog(){//隐藏对话框
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 通过level去更新voice的图片
     * @param level 1-7
     */
    public void updateVoiceLevel(int level){
        //switch使代码太过冗余
        if(mDialog!=null&&mDialog.isShowing()){
            System.out.println("---level--"+level);
            int resId=mContext.getResources().getIdentifier("v"+level,
                    "drawable",mContext.getPackageName());
            System.out.println("---resId+"+resId);
            mVoice.setImageResource(resId);
        }
    }
}
