import React from 'react';
import SearchArea from 'widget/search-area';
import { Form, Input, Col, Row, Button } from 'antd';
import Table from 'widget/table';
import CustomTable from 'components/Widget/custom-table';
import config from 'config';
import { connect } from 'dva';
const FormItem = Form.Item;

class InvoiceResultInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      visible: false,
      data: [],
      //表格
      columns: [
        {
          //查看
          title: '查看',
          dataIndex: 'lookDetail',
          key: 'lookDetail',
          align: 'center',
          width: 200,
        },
        {
          //发票代码
          title: '发票代码',
          dataIndex: 'invoiceNumber',
          key: 'invoiceNumber',
          align: 'center',
          width: 100,
          width: 200,
        },
        {
          //发票号码
          title: '发票号码',
          dataIndex: 'invoiceCode',
          key: 'invoiceCode',
          align: 'center',
          width: 200,
        },
        {
          //发票类型
          title: '发票类型',
          dataIndex: 'invoiceType',
          key: 'invoiceType',
          align: 'center',
          width: 200,
        },
        {
          //开票状态
          title: '开票状态',
          dataIndex: 'invoiceStatus',
          key: 'invoiceStatus',
          align: 'center',
          width: 200,
        },
        {
          //客户编号
          title: '客户编号',
          dataIndex: 'clientCode',
          key: 'clientCode',
          align: 'center',
          width: 200,
        },
        {
          //客户名称
          title: '客户名称',
          dataIndex: 'clientName',
          key: 'clientName',
          align: 'center',
          width: 200,
        },
        {
          //纳税人识别号
          title: '纳税人识别号',
          dataIndex: 'clientTaxNum',
          key: 'clientTaxNum',
          align: 'center',
          width: 200,
        },
        {
          //开票日期
          title: '开票日期',
          dataIndex: 'invoiceDate',
          key: 'invoiceDate',
          align: 'center',
          width: 200,
        },
        {
          //总金额价税合计
          title: '总金额价税合计',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          align: 'center',
          width: 200,
        },
        {
          //不含税金额
          title: '不含税金额',
          dataIndex: 'excludingTaxAmount',
          key: 'excludingTaxAmount',
          align: 'center',
          width: 200,
        },
        {
          //税额
          title: '税额',
          dataIndex: 'taxes',
          key: 'taxes',
          align: 'center',
          width: 200,
        },
        {
          //开票人
          title: '开票人',
          dataIndex: 'drawer',
          key: 'drawer',
          align: 'center',
          width: 200,
        },
      ],
      //分页
      pagination: {
        total: 0,
        onChange: this.onChangePager,
        current: 1,
        onShowSizeChange: this.onChangePageSize,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSize: 10,
        showTotal: (total, range) =>
          this.$t(
            { id: 'common.show.total' },
            { range0: `${range[0]}`, range1: `${range[1]}`, total }
          ),
      },
    };
  }

  render() {
    const { visible, data, columns, loading, pagination } = this.state;
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
        </div>
        <div className="Table_div" style={{ marginTop: '30px' }}>
          <Table
            columns={columns}
            dataSource={data}
            pagination={pagination}
            loading={loading}
            bordered
            size="middle"
          />
        </div>
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
    );
  }
}

export default connect()(InvoiceResultInfo);
