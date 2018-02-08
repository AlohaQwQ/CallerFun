package com.xixi.finance.callerfun.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.xixi.finance.callerfun.R;
import com.xixi.finance.callerfun.constant.APIKey;
import com.xixi.finance.callerfun.presenter.BasePresenter;
import com.xixi.finance.callerfun.presenter.user.LoginPresenter;
import com.xixi.finance.callerfun.ui.view.user.ILoginView;
import com.xixi.finance.callerfun.util.PasswordUtil;
import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.util.Map;

import aloha.shiningstarbase.util.CommonJSONParser;
import butterknife.BindView;
import butterknife.OnClick;
import cn.chutong.sdk.common.util.NetworkUtil;
import cn.chutong.sdk.common.util.TypeUtil;

/**
 * Created by Aloha <br>
 * -explain
 *
 * @Date 2018/1/29 11:30
 */
public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter> implements ILoginView {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.check_show_hide_password)
    CheckBox checkShowHidePassword;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private String value;
    private String username;
    private String password;

    private boolean isLoginEnable = false;
    private boolean isRegisterAndLogin = false;
    private boolean userJoinActivityH5 = false;


    @Override
    protected LoginPresenter CreatePresenter() {
        return new LoginPresenter();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        initUI();
    }

    private void initUI() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (input.startsWith("0")) {
                    etUsername.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkLoginEnable();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkLoginEnable();
            }
        });

        etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16), new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (PasswordUtil.isChinese(source.toString())) {
                    return "";
                } else {
                    return source.toString();
                }
            }
        }});

        checkShowHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().toString().trim().length());
            }
        });

        btnLogin.setClickable(isLoginEnable);
        btnLogin.setEnabled(isLoginEnable);
    }


    private void checkLoginEnable() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(password)) {
            isLoginEnable = true;
        } else {
            isLoginEnable = false;
        }
        btnLogin.setClickable(isLoginEnable);
        btnLogin.setEnabled(isLoginEnable);
    }

    @OnClick({R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (NetworkUtil.isNetworkAvaliable(this)) {
                    if (!TextUtils.isEmpty(password)) {
                        mPresenter.userLogin(username, password);
                        showProgressView("登录中");
                    } else {
                        showToast("登录失败");
                    }
                } else {
                    showToast(this.getString(R.string.error_network));
                }
                break;
        }
    }

    @Override
    public void onLoginSuccess(String response, String status, String message) {
        hideProgressView();
        Intent intent = new Intent();
        if (BasePresenter.RESPONSE_STATUS_SUCCESS.equals(status)) {
            showToast("登录成功！");
            intent.setAction(MainActivity.ACTION_REFRESH);
            sendBroadcast(intent);

            Map<String, Object> responseMap = null;
            if (!TextUtils.isEmpty(response))
                responseMap =  new CommonJSONParser().parse(response);
            if(responseMap!=null)
                PersistentDataCacheEntity.getInstance().setToken(TypeUtil.getString(responseMap.get(APIKey.USER_TOKEN),""));
            PersistentDataCacheEntity.getInstance().setName(username);
            finish();
        } else {
            showToast(message);
        }
    }
}
