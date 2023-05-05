import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import {Button, List, message, Space} from 'antd';
import {
  getInterfaceInfoByIdUsingGET,
  listInterfaceInfoByPageUsingGET, onlineInterfaceInfoUsingPOST
} from '@/services/ljh-backend/interfaceInfoController';
import {HeartOutlined, MessageOutlined, PieChartFilled, SmileTwoTone} from "@ant-design/icons";
import {
  buyInterfaceUsingPOST,
  followInterfaceUsingPOST,
  getFollowUsingGET
} from "@/services/ljh-backend/userController";
import {getUserUsingGET} from "@/services/ljh-backend/keyController";
import {Link} from "@@/exports";

/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [currentRow, setCurrentRow] = useState<number>();
  const [dataa, setDataa] = useState<API.User>();


  const loadData = async (current = 1, pageSize = 5) => {
    setLoading(true);
    try {
      const res = await listInterfaceInfoByPageUsingGET({
        current,
        pageSize,

      });

      const ress = await getUserUsingGET({
      });

      setDataa(ress.data);

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



  const interfaceInfoById = async (record: API.followInterfaceUsingPOSTParams) => {
    const hide = message.loading('订阅中');
    if (!record) return true;
    try {
      const res = await followInterfaceUsingPOST({
        id: record.id
      });



      setCurrentRow(res.data);

      hide();
      message.success('订阅成功');
      return true;
    } catch (error: any) {
      hide();
      message.error('订阅失败，' + error.message);
      return false;
    }
  };




  const buyInterface = async (record: API.buyInterfaceUsingPOSTParams) => {
    const hide = message.loading('购买中');
    if (!record) return true;
    try {
      const resss = await buyInterfaceUsingPOST({
        id: record.id
      });


      hide();
      message.success(resss.data);
      return true;
    } catch (error: any) {
      hide();
      message.error('购买失败咯！！！' + error.message);
      return false;
    }
  };









  return (
    <PageContainer title="在线接口大全">
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;




          return (

            <List.Item actions={[

              <div >0.01元/一条</div>,


              item.status === 0 ? <a key={item.id}


            >禁止测试</a>: null,

              item.status === 1 ? <Button
                  type="primary"
                  key="offline"
                  ghost
                  onClick={() => {

                    interfaceInfoById(item);
                    setCurrentRow(item.id);


                  }}


                  href= {apiLink}

                >
                <Link  to= {apiLink}>可测试</Link>



                </Button> : null,


              item.status === 0 ? <a key={item.id}

              >不可购买</a>: null,



              item.status === 1 ?  <Button
                type="primary"
                key="offline"
                ghost
                onClick={() =>
                  {

                    buyInterface(item);

                  }

                }
              >
                点击购买
              </Button> : null,




            ]
            }


            >

              <Space>
                <SmileTwoTone />


              </Space>

              <List.Item.Meta
                title={<a >{ item.description}</a>}
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
