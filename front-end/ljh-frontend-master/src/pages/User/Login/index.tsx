import Footer from '@/components/Footer';
import {
  AlipayCircleOutlined,
  LockOutlined,
  MobileOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormText,
} from '@ant-design/pro-components';
import { history, useModel } from '@umijs/max';
import {Alert, message, notification, Tabs} from 'antd';
import React, { useState } from 'react';
import styles from './index.less';
import {
  codeLoginUsingPOST,
  sendCodeUsingGET,
  userLoginUsingPOST
} from '@/services/ljh-backend/userController';
import { Link } from 'umi';




const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};
const Login: React.FC = () => {
  const [userLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const { setInitialState } = useModel('@@initialState');


  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      // 登录

      let res = null;
      if (type === 'account') {
        res = await userLoginUsingPOST({ ...values });
      } else {
        res = await codeLoginUsingPOST({...values});
      }



      if (res.data) {
        const urlParams = new URL(window.location.href).searchParams;
        history.push(urlParams.get('redirect') || '/');
        setInitialState({
          loginUser: res.data

        });



        const userToken = res.data.userToken.toString();
        sessionStorage.setItem("token", userToken);
      }
    } catch (error) {
      const defaultLoginFailureMessage = '登录失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };
  const { status, type: loginType } = userLoginState;


  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          logo={<img alt="logo" src="/ljh-01.png" />}
          title="WowApi 开放平台"
          subTitle={'        '}
          initialValues={{
            autoLogin: true,
          }}
          actions={[
            '其他登录方式 :',
            <AlipayCircleOutlined key="AlipayCircleOutlined" className={styles.icon} />,
            <TaobaoCircleOutlined key="TaobaoCircleOutlined" className={styles.icon} />,
            <WeiboCircleOutlined key="WeiboCircleOutlined" className={styles.icon} />,
          ]}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserLoginRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账户密码登录',
              },
              {
                key: 'mobile',
                label: '手机号登录',
              },
            ]}
          />

          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的用户名和密码(admin/ant.design)'} />
          )}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon} />,
                }}
                placeholder={'用户名: '}
                rules={[
                  {
                    required: true,
                    message: '用户名是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon} />,
                }}
                placeholder={'密码: '}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                ]}
              />
            </>
          )}

          {status === 'error' && loginType === 'mobile' && <LoginMessage content="验证码错误" />}
          {type === 'mobile' && (
            <>
              <ProFormText
                fieldProps={{
                  size: 'large',
                  prefix: <MobileOutlined className={styles.prefixIcon} />,
                }}
                name="userPhone"
                placeholder={'请输入手机号！'}
                rules={[
                  {
                    required: true,
                    message: '手机号是必填项！',
                  },
                  {
                    pattern: /^1\d{10}$/,
                    message: '不合法的手机号！',
                  },
                ]}
              />
              <ProFormCaptcha
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon} />,
                }}
                captchaProps={{
                  size: 'large',
                }}
                placeholder={'请输入验证码！'}
                captchaTextRender={(timing, count) => {
                  if (timing) {
                    return `${count} ${'秒后重新获取'}`;
                  }
                  return '获取验证码';
                }}
                name="code"

                phoneName="userPhone"
                rules={[
                  {
                    required: true,
                    message: '验证码是必填项！',
                  },
                ]}
                onGetCaptcha={async (userPhone) => {
                  const result = await sendCodeUsingGET({
                    userPhone,
                  });
                  // @ts-ignore
                  if (result === false) {
                    return;
                  }
                  message.success('模拟获取验证码成功！验证码为:' + result.data);
                }}
              />
            </>
          )}
          <div
            style={{
              marginBottom: 24,
            }}
          >


            <div  style={{
              float: 'left',
            }}>
            <Link to="/user/register">用户注册</Link>

            </div>

            <a
              style={{
                float: 'right',

              }}


            >
              忘记密码 ?


            </a>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};
export default Login;
