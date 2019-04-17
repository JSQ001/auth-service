import React from 'react';
import { Form, Row, Col, Input, Select, DatePicker, Button } from 'antd';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';
import FundLov from '../../fund-components/fund-lov';
import FundEditablTable from '../../fund-components/fund-editable-table';

import 'styles/fund/account.scss';

const FormItem = Form.Item;

class NewFundTransferList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: '序号',
          dataIndex: 'sequenceNumber',
          editable: false,
          width: 50,
        },
        {
          title: '调入公司',
          dataIndex: 'company',
          type: 'lov',
          lovCode: 'company',
          lovValueKey: 'id',
          lovLabelKey: 'companyName',
          lovExtraParams: { setOfBooksId: '1083762150064451585' },
          lovSingle: true,
          editable: true,
          width: 100,
        },
        {
          title: '调入账号',
          dataIndex: 'account',
          editable: true,
          width: 100,
        },
        {
          title: '调入银行',
          dataIndex: 'bank',
          editable: true,
          width: 100,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          editable: true,
          width: 100,
        },
        {
          title: '币种',
          dataIndex: 'currency',
          editable: false,
          width: 100,
        },
        {
          title: '摘要',
          dataIndex: 'remark',
          editable: false,
          width: 100,
        },
        {
          title: '调拨申请单号',
          dataIndex: 'requisitionNumber',
          editable: false,
          width: 100,
        },
        {
          title: '支付状态',
          dataIndex: 'payStatus',
          editable: true,
          width: 100,
        },
      ],
    };
  }

  /**
   * onRef调用子组件方法
   */
  onRef = ref => {
    this.child = ref;
  };

  addLine = () => {
    this.child.handleAdd();
  };

  deleteLine = () => {};

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;

    const { columns } = this.state;

    return (
      <div className="train">
        <div className="table-header">
          <Form>
            <Row>
              <Col span={5}>
                <FormItem label="单据编号" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('documentNumber', {
                    initialValue: 11111,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="所属部门" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('company', {
                    initialValue: { id: '', companyName: '' },
                  })(
                    <Lov
                      code="company"
                      valueKey="id"
                      labelKey="companyName"
                      extraParams={{ setOfBooksId: '1083762150064451585' }}
                      single
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="所属公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('department', {
                    initialValue: {},
                  })(<FundLov />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="付款方式" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentMethodDesc', {
                    initialValue: [],
                  })(<Select labelInValue placeholder="请选择" />)}
                </FormItem>
              </Col>
              <Col span={5}>
                <FormItem label="调出账号" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('accountNumber', {
                    initialValue: 11111,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="调出账户" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('account', {
                    initialValue: 11111,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="单据日期" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('date', {})(<DatePicker />)}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="制单人" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('account', {
                    initialValue: '制单人',
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={11}>
                <FormItem label="备注">
                  {getFieldDecorator('mark', {
                    initialValue: '备注',
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </div>
        <div className="table-header-buttons" style={{ marginBottom: '20px' }}>
          <Row>
            <Col span={12}>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.addLine();
                }}
              >
                新增行
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                type="danger"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.deleteLine();
                }}
              >
                删除行
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.createFromApply();
                }}
              >
                从申请单创建
              </Button>
              <Button
                style={{ marginRight: '10px' }}
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.save();
                }}
              >
                保存
              </Button>
              <Button
                type="primary"
                onClick={e => {
                  e.preventDefault();
                  e.stopPropagation();
                  this.submit();
                }}
              >
                提交
              </Button>
            </Col>
          </Row>
        </div>
        <FundEditablTable onRef={this.onRef} columns={columns} />
      </div>
    );
  }
}

/**
 * 建立组件和数据的映射关系 注意state必传 返回的是需要绑定的model
 * @param {*} state
 */
function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
/**
 * 关联 model
 */
export default connect(map)(Form.create()(NewFundTransferList));
