import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import {
  Button,
  Card,
  Descriptions,
  Form,
  message,
  Input,
  Spin,
  Divider,
  Switch,
  Row,
  Col,
  Statistic,
  Space,
  Badge
} from 'antd';
import {
  getInterfaceInfoByIdUsingGET,
  invokeInterfaceInfoUsingPOST,
} from '@/services/ljh-backend/interfaceInfoController';
import {history, useParams} from '@@/exports';
import hljs from 'highlight.js'
import 'highlight.js/styles/vs2015.css'


/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfoVO>();


  const [invokeRes, setInvokeRes] = useState<any>();
  const [invokeLoading, setInvokeLoading] = useState(false);

  const params = useParams();

  const loadData = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }

    setLoading(true);
    try {
      const res = await getInterfaceInfoByIdUsingGET({
        id: Number(params.id),
      });


      setData(res.data);


    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();

    // 配置 highlight.js
    hljs.configure({
      // 忽略未经转义的 HTML 字符
      ignoreUnescapedHTML: true
    })
    // 获取到内容中所有的code标签
    const codes = document.querySelectorAll('.dg-html pre code')
    codes.forEach((el) => {
      // 让code进行高亮
      hljs.highlightElement(el as HTMLElement)
    })

  }, []);



  const content = `


	<h3>示例：输入自己的密钥信息：</h3>
	<pre>
	  <code>ljh:
  client:
    secret-key: qweqw
    access-key: qweqw
</code>


	</pre>

		<h3>使用客户端，调用接口：</h3>
<pre>
	  <code>
    @Resource
    private LjhClient ljhClient;

    @GetMapping("/test1")
    public void test(){

        //调用可用接口
        String result = ljhClient.getKnow();


    }

    </code>


	</pre>


	`




  const onFinish = async (values: any) => {
    if (!params.id) {
      message.error('接口不存在');
      return;
    }
    setInvokeLoading(true);
    try {
      const res = await invokeInterfaceInfoUsingPOST({
        id: params.id,
        ...values,
      }


      );

      const ress = await getInterfaceInfoByIdUsingGET({
        id: Number(params.id),
      });


      setData(ress.data);

      setInvokeRes(res.data);
      message.success('请求成功');
    } catch (error: any) {
      message.error('操作失败，' + error.message);
    }
    setInvokeLoading(false);
  };





  const [isLoading, setIsLoading] = useState(false);

  const handleDownload = async () => {
    setIsLoading(true);

    try {
      const response = await fetch('/api/user/downLoad', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'
        },
      });

      const blob = await response.blob();
      const url = window.URL.createObjectURL(new Blob([blob]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'ljh-client-sdk-0.0.1-SNAPSHOT.jar');
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);

      setIsLoading(false);
    } catch (error) {
      console.error(error);

      setIsLoading(false);
    }
  };



  return (
    <PageContainer title="查看接口文档">


          <Card >
            {data ? (
              <Descriptions title={data.name} column={1}>
                <Descriptions.Item label="接口状态"> {<Switch disabled={true} checked={data.status === 0 ? false : true} />}</Descriptions.Item>
                <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
                <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
                <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>

                <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
                <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
                <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
                <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
                <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
              </Descriptions>
            ) : (
              <>接口不存在</>
            )}
          </Card>


      <Divider />
        {data ? (
          <Row gutter={16}>


            <Col span={12}>
              <Statistic title="接口调用次数" value= {data.totalNum}     valueStyle={{ color: '#3f8600' }}/>
            </Col>
            <Col span={12}>
              <Statistic title="接口剩余次数" value={data.leftNum}  valueStyle={{ color: '#cf1322' }}/>


              <Space direction="vertical" size="middle" style={{ width: '100%' }}>

                <Badge.Ribbon text="小提示" color="magenta">
                  <Card title="免费提供10次次数！" size="small">

                  </Card>
                </Badge.Ribbon>
              </Space>


            </Col>
            <Col span={12}>
              <Statistic title="接口调用次数" value={112893} loading />
            </Col>
          </Row>
        ) : (
          <>接口不存在</>
        )}





      <Card title="在线调用">
        <Form name="invoke" layout="vertical" onFinish={onFinish}>
          <Form.Item label="请求参数" name="userRequestParams">
            <Input.TextArea />
          </Form.Item>
          <Form.Item wrapperCol={{ span: 16 }}>
            <Button type="primary" >
              调用
            </Button>


          </Form.Item>
        </Form>


      </Card>
      <Divider />
      <Card title="返回结果" loading={invokeLoading}>
        {invokeRes}
      </Card>


      <Badge.Ribbon text="开发文档" color="#2db7f5">


        <Card>

          <button   color="primary"  onClick={handleDownload} disabled={isLoading}>
            {isLoading ? '加载中...' : '下载SDK'}
          </button>

          <div className="dg-html">

            <div dangerouslySetInnerHTML={{ __html: content }} />
          </div>
        </Card>
      </Badge.Ribbon>

    </PageContainer>
  );
};

export default Index;
