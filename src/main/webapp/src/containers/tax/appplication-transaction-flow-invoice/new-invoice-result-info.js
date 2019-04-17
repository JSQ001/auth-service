import React from 'react';
import { connect } from 'dva';
import { Form, Input, Col, Row, Button } from 'antd';
import { Tabs } from 'antd';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
import TransactionDetails from './transaction-details';
import InvoiceDetails from './invoice-details';

class NewInvoiceresultinfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const {} = this.state;
    return (
      <div className="train">
        <div className="table-header">
          <Form>
            <Row>
              <Col span={5}>
                <FormItem label="申请编号" style={{ marginBottom: '0px' }}>
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="纳税主体" style={{ marginBottom: '0px' }}>
                  {/* {getFieldDecorator('documentNumber', {
                    initialValue: '',
                  }) */}
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="申请人" style={{ marginBottom: '0px' }}>
                  {/* {getFieldDecorator('documentNumber', {
                    initialValue: '',
                  }) */}
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="创建日期" style={{ marginBottom: '0px' }}>
                  {/* {getFieldDecorator('documentNumber', {
                    initialValue: '',
                  }) */}
                  <Input disabled />
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={5}>
                <FormItem label="发票类型" style={{ marginBottom: '0px' }}>
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="盘商接口" style={{ marginBottom: '0px' }}>
                  {/* {getFieldDecorator('documentNumber', {
                    initialValue: '',
                  }) */}
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="状态" style={{ marginBottom: '0px' }}>
                  {/* {getFieldDecorator('documentNumber', {
                    initialValue: '',
                  }) */}
                  <Input disabled />
                </FormItem>
              </Col>
              <Col span={11}>
                <FormItem label="备注">
                  {/* {getFieldDecorator('mark', {
                    initialValue: '',
                  })( */}
                  <Input placeholder="请输入" />
                  {/* // )} */}
                </FormItem>
              </Col>
            </Row>
          </Form>
          <Tabs>
            <TabPane tab="交易流水明细" key="1">
              <TransactionDetails />
            </TabPane>
            <TabPane tab="发票明细" key="2">
              <InvoiceDetails />
            </TabPane>
          </Tabs>
          <div className="table-header" style={{ marginBottom: '20px' }}>
            <div className="table-header-buttons">
              <Button type="primary" style={{ marginRight: '10px' }}>
                保存
              </Button>
              <Button style={{ marginRight: '10px' }}>返回</Button>
              <Button style={{ marginRight: '10px' }}>提交</Button>
              <Button style={{ marginRight: '10px' }}>删除</Button>
              <Button>附件上传</Button>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

function callback(key) {}
export default connect()(NewInvoiceresultinfo);
