import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import {Button, List, message, Result} from 'antd';
import {
  listInterfaceInfoByPageUsingGET,
  listUserInterfaceInfoByPageUsingGET
} from '@/services/ljh-backend/interfaceInfoController';
import {SmileOutlined} from "@ant-design/icons";
import hljs from "highlight.js";

/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  const apiLink = `/user/buyinterface`;






  const loadData = async (current = 1, pageSize = 5) => {
    setLoading(true);
    try {
      const res = await listUserInterfaceInfoByPageUsingGET({
        current,
        pageSize,
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);



    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();


  }, []);




  return (

    list.length === 0 ?  <Result
      icon={<SmileOutlined />}
      title="你还没有测试接口哟哈哈哈哈！"
      extra={<Button type="primary"
                     href= {apiLink} >

        去逛逛

      </Button>}
    /> :





    <PageContainer title="我的测试">
      <List




        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {




          const apiLink = `/interface_info/${item.id}`;
          return (
            <List.Item actions={[<a key={item.id} href={apiLink}>查看</a>]}>
              <List.Item.Meta
                title={<a href={apiLink}>{item.name}</a>}
                description={item.description}
              />
            </List.Item>
          );
        }}
        pagination={{
          // eslint-disable-next-line @typescript-eslint/no-shadow
          showTotal(total: number) {
            return '总数：' + total;
          },
          pageSize: 5,
          total,
          onChange(page, pageSize) {
            loadData(page, pageSize);
          },
        }}
      />
    </PageContainer>
  );












};

export default Index;
