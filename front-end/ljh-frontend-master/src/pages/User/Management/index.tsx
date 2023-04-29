import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import {Button, Card, Descriptions, Form, message, Alert, Divider, Drawer, Avatar, Badge, Popconfirm} from 'antd';

import {getUserUsingGET, updateSettingUsingPOST} from "@/services/ljh-backend/keyController";
import {values} from "lodash";

/**
 * 主页
 * @constructor
 */



const Index: React.FC = () => {
  const [data, setData] = useState<API.User>();
  const [invokeRes, setInvokeRes] = useState<any>();
  const [invokeLoading, setInvokeLoading] = useState(false);


  const loadData = async () => {

    try {
      const res = await getUserUsingGET({
      });


      setData(res.data);

    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
  };

  useEffect(() => {
    loadData();
  }, []);


  const onFinish = async (values: any) => {


    if (!data.id) {
      message.error('用户不存在');
      return;
    }

    setInvokeLoading(true);
    try {
      const res = await updateSettingUsingPOST({
        id: data.id,

        ...values,
      });


      const ress = await getUserUsingGET({
      });
      setData(ress.data);

      setInvokeRes(res.data);
      message.success('修改成功');



    } catch (error: any) {
      message.error('修改失败，' + error.message);
    }
    setInvokeLoading(false);
  };



    const [open, setOpen] = useState(false);

    const showDrawer = () => {
      setOpen(true);
    };

    const onClose = () => {
      setOpen(false);


    };




  const interfaceInfoById = async (record: API.BaseResponseUser) => {
    const hide = message.loading('修改中');
    if (!record) return true;
    try {
      const res = await updateSettingUsingPOST({
        id: data.id,
        ...values,

      });

      const ress = await getUserUsingGET({
      });
      setData(ress.data);

      setInvokeRes(res.data);


      message.success('修改成功');


      hide();
      return true;
    } catch (error: any) {
      hide();
      message.error('修改失败，' + error.message);
      return false;
    }
  };




  const confirm = (data: API.BaseResponseUser) =>{



    interfaceInfoById(data);

  }
  new Promise((confirm) => {


    setTimeout(() => confirm(null), 3000);

  });




  return (


    <PageContainer title="查看个人信息">


      <Card>


        {data ? (


          <Descriptions title={"个人详情"} column={1}>
          <Descriptions.Item label="用户名">{data.userName}</Descriptions.Item>
          <Descriptions.Item label="账号">{data.userAccount}</Descriptions.Item>
          <Descriptions.Item label="accessKey">{data.accessKey.substring(0,3)}**************{data.accessKey.slice(-3)}</Descriptions.Item>
          <Descriptions.Item label="secretKey">{data.secretKey.substring(0,3)}**************{data.secretKey.slice(-3)}</Descriptions.Item>
          </Descriptions>
          ) : (
          <>用户不存在</>
          )}
      </Card>

      <Divider/>
      <Card title="修改密钥">
        <Form name="invoke" layout="vertical" onFinish={onFinish}>

          <Form.Item wrapperCol={{span: 16}}>

            <>
              <Button type="primary" onClick={showDrawer}>
                查看密钥




              </Button>
              <Drawer title="Key information" placement="right" onClose={onClose} open={open}>
                <Avatar src={data?.userAvatar} />


                <Card>

                  {data ? (


                    <Descriptions title={"密钥信息"} column={1}>
                      <Descriptions.Item label="accessKey">{data.accessKey}</Descriptions.Item>
                      <Descriptions.Item label="secretKey">{data.secretKey}</Descriptions.Item>
                    </Descriptions>
                  ) : (
                    <>用户不存在</>
                  )}
                </Card>





                <Badge.Ribbon text="啦啦啦啦" color="#2db7f5">
                  <Card>推开窗户举起望远镜</Card>
                </Badge.Ribbon>



              </Drawer>
            </>

            <span>                               </span>


            <Popconfirm
              title="继续此操作吗"
              description="22"
              onConfirm={confirm}
              onOpenChange={() => console.log('open change')}
            >
              <Button type="primary" htmlType="submit">
                修改密钥
              </Button>
            </Popconfirm>








          </Form.Item>
        </Form>
      </Card>
      <Divider/>
      <Alert
        message="安全提示"
        banner
        description="您的 API 密钥代表您的账号身份和所拥有的权限，使用 API 密钥可以操作您名下的所有平台上的资源。
                 为了您的财产和服务安全，请妥善保存密钥，请勿通过任何方式上传或者分享您的密钥信息。"

      />
    </PageContainer>
  );
};

export default Index;
