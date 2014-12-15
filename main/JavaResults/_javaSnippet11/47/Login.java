package com.ghost.xianyu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ghost.xianyu.encrytion.AESEncryptor;
import com.ghost.xianyu.internet.HttpConnection;
import com.ghost.xianyu.internet.NetworkConnection;
import com.ghost.xianyu.util.ValidityCheck;

public class Login extends Activity
  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
  static final int EMPTY_PASSWORD = -1;
  static final int EMPTY_PHONE = -2;
  static final int FAILURE = 2;
  static final int FREEZE_ACCOUNT = 3;
  static final int NO_NETWORK = -3;
  static final int SUCCESS = 1;
//  static final int WRONG_PHONE; modified
  static final int WRONG_PHONE = -1;
  Handler authHd = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case -2:
        Toast.makeText(Login.this, "请输入手机号", 0).show();
        return;
      case -1:
        Toast.makeText(Login.this, "请输入密码", 0).show();
        return;
      case 0:
        Toast.makeText(Login.this, "输入的手机号格式不正确", 0).show();
        return;
      case 1:
        Login.this.authPd.dismiss();
        Login.this.saveUserInfo();
        Login.this.jumpToMain();
        return;
      case -3:
        Login.this.authPd.dismiss();
        Toast.makeText(Login.this, "网络连接不可用", 0).show();
        return;
      case 2:
        Login.this.authPd.dismiss();
        Toast.makeText(Login.this, "手机号或密码不正确", 0).show();
        return;
      case 3:
      }
      Login.this.authPd.dismiss();
      Toast.makeText(Login.this, "您的账号已经被冻结，如有疑问，请联系咸鱼网所在大学分店管理员", 0).show();
    }
  };
  ProgressDialog authPd;
  TextView findPasswordTv;
  boolean isRemember;
  Button loginBtn;
  String password;
  EditText passwordEt;
  EditText phoneEt;
  String phoneNum;
  TextView registerTv;
  CheckBox rememberCb;

  public void authen()
  {
    this.phoneNum = this.phoneEt.getText().toString().trim();
    this.password = this.passwordEt.getText().toString().trim();
    if (this.phoneNum.equals(""))
    {
      this.authHd.sendEmptyMessage(-2);
      return;
    }
    if (!ValidityCheck.isMobileNO(this.phoneNum))
    {
      this.authHd.sendEmptyMessage(0);
      return;
    }
    if (this.password.equals(""))
    {
      this.authHd.sendEmptyMessage(-1);
      return;
    }
    if (!NetworkConnection.isConnected(this))
    {
      this.authHd.sendEmptyMessage(-3);
      return;
    }
    this.authPd = new ProgressDialog(this);
    this.authPd.setMessage("正在登录...");
    this.authPd.show();
    new Thread()
    {
      public void run()
      {
        try
        {
          String str = HttpConnection.post_login(Login.this.phoneNum, Login.this.password);
          if (str.equals("1"))
          {
            Login.this.authHd.sendEmptyMessage(1);
            return;
          }
          if (str.equals("-1"))
          {
            Login.this.authHd.sendEmptyMessage(3);
            return;
          }
        }
        catch (Exception localException)
        {
          Login.this.authHd.sendEmptyMessage(2);
          localException.printStackTrace();
          return;
        }
        Login.this.authHd.sendEmptyMessage(2);
      }
    }
    .start();
  }

  public void jumpToFindPassword()
  {
    startActivity(new Intent(this, PasswrodRetake.class));
  }

  public void jumpToMain()
  {
    startActivity(new Intent(this, Home.class));
    finish();
  }

  public void jumpToRegister()
  {
    startActivity(new Intent(this, Register.class));
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    this.isRemember = paramBoolean;
  }

  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default:
      return;
    case 2131296312:
      authen();
      return;
    case 2131296313:
      jumpToRegister();
      return;
    case 2131296314:
    }
    jumpToFindPassword();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(2130903044);
    this.phoneEt = ((EditText)findViewById(2131296309));
    this.passwordEt = ((EditText)findViewById(2131296310));
    this.rememberCb = ((CheckBox)findViewById(2131296311));
    this.findPasswordTv = ((TextView)findViewById(2131296314));
    this.registerTv = ((TextView)findViewById(2131296313));
    this.loginBtn = ((Button)findViewById(2131296312));
    this.rememberCb.setOnCheckedChangeListener(this);
    this.findPasswordTv.setOnClickListener(this);
    this.registerTv.setOnClickListener(this);
    this.loginBtn.setOnClickListener(this);
    if (paramBundle != null)
    {
      this.phoneNum = paramBundle.getString("phone");
      this.password = paramBundle.getString("password");
      this.isRemember = paramBundle.getBoolean("isRemember");
    }
    while (true)
    {
      if (!this.phoneNum.equals(""))
      {
        this.phoneEt.setText(this.phoneNum);
        if (!this.isRemember)
          break;
        this.passwordEt.setText(this.password);
        this.rememberCb.setChecked(true);
      }
//      return;  导致下面代码不可达，故注释掉 modified
      SharedPreferences localSharedPreferences = getSharedPreferences("user_info", 0);
      this.isRemember = localSharedPreferences.getBoolean("isRemember", false);
      this.phoneNum = localSharedPreferences.getString("phone", "");
      if (this.isRemember)
        try
        {
          this.password = AESEncryptor.decrypt(AESEncryptor.MAK, localSharedPreferences.getString("password", ""));
        }
        catch (Exception localException)
        {
          this.password = "";
          localException.printStackTrace();
        }
      else
        this.password = "";
    }
    this.rememberCb.setChecked(false);
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    getMenuInflater().inflate(2131230720, paramMenu);
    return true;
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putString("phone", this.phoneEt.getText().toString());
    paramBundle.putString("password", this.passwordEt.getText().toString());
    paramBundle.putBoolean("isRemember", this.rememberCb.isChecked());
  }

  public void saveUserInfo()
  {
    SharedPreferences.Editor localEditor = getSharedPreferences("user_info", 0).edit();
    localEditor.putString("sessionId", HttpConnection.php_sessid);
    localEditor.putBoolean("isRemember", this.isRemember);
    localEditor.putBoolean("isLogin", true);
    localEditor.putString("phone", this.phoneNum);
    if (this.isRemember);
    while (true)
    {
      try
      {
        localEditor.putString("password", AESEncryptor.encrypt(AESEncryptor.MAK, this.password));
        localEditor.commit();
        return; 
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
//        continue;  Unreachable code错误，故注释掉
      }
      localEditor.putString("password", "");
    }
  }
}

/* Location:           /Users/idCarMack/program/private/xianyu/XianYu/classes-dex2jar.jar
 * Qualified Name:     com.ghost.xianyu.Login
 * JD-Core Version:    0.6.2
 */