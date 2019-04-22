import React, { Component } from 'react';
import { Form, Row, Col, Input, Button, Icon, DatePicker, Select, message } from 'antd';
import '../../fund-components/components.scss';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';

const { Option } = Select;

class AccountStatementSearch extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
    };
  }

  /**
   * 提交表单
   */
  handleSearch = e => {
    const {
      form: { validateFields },
      submitHandle,
    } = this.props;
    e.preventDefault();
    validateFields((err, values) => {
      submitHandle(values);
    });
  };

  /**
   * 清空表单
   */
  handleReset = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  };

  /**
   * 收起和展开
   */
  toggle = () => {
    const { expand } = this.state;
    this.setState({ expand: !expand });
  };

  /**
   * 根据银行账号对应所属银行
   */
  handleSelectChange = value => {
    const {
      form: { setFieldsValue },
    } = this.props;
    setFieldsValue({
      bankBelong: value.openBankName,
    });
  };

  checkTime = () => {
    const {
      form: { getFieldValue },
    } = this.props;
    console.log(getFieldValue('compontyId'));
    const startTime = getFieldValue('accountDateFrom');
    const endTime = getFieldValue('accountDateTo');
    if (endTime < startTime) {
      message.error('结束日期小于开始日期');
    }
  };

  checkMoney = () => {
    const {
      form: { getFieldValue },
    } = this.props;
    const startMoney = getFieldValue('amountFrom');
    const endMoney = getFieldValue('amountTo');
    if (startMoney > endMoney) {
      message.error('发生金额至大于发生金额从');
    }
  };

  render() {
    const { expand } = this.state;
    const {
      form: { getFieldDecorator, getFieldValue },
      company: { setOfBooksId },
    } = this.props;
    console.log(getFieldValue('compontyId'));
    const companyId = getFieldValue('compontyId') ? getFieldValue('compontyId').id : '';
    return (
      <Form className="ant-advanced-search-form" onSubmit={this.handleSearch}>
        <Row gutter={24}>
          <Col span={6}>
            <Form.Item label="公司" colon={false}>
              {getFieldDecorator(`compontyId`, {})(
                <Lov
                  code="company"
                  valueKey="id"
                  labelKey="name"
                  single
                  extraParams={{ setOfBooksId }}
                />
              )}
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="银行账号" placeholder="请选择（必输）" colon={false}>
              {getFieldDecorator(`accountId`, {})(
                <Lov
                  code="bankaccount_choose"
                  valueKey="id"
                  labelKey="accountNumber"
                  single
                  onChange={this.handleSelectChange}
                  extraParams={{ companyId }}
                />
              )}
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="所属银行" colon={false}>
              {getFieldDecorator(`bankBelong`, {
                rules: [
                  {
                    // message: 'Input something!',
                  },
                ],
              })(<Input placeholder="根据帐号自动带入" />)}
            </Form.Item>
          </Col>
          <Col span={3}>
            <Form.Item label="发生日期从" colon={false}>
              {getFieldDecorator(`accountDateFrom`, {})(<DatePicker placeholder="请选择" />)}
            </Form.Item>
          </Col>
          <Col span={3}>
            <Form.Item label="发生日期至" colon={false}>
              {getFieldDecorator(`accountDateTo`, {})(
                <DatePicker placeholder="请选择" onBlur={this.checkTime} />
              )}
            </Form.Item>
          </Col>
          <Col span={6} style={{ display: expand ? 'block' : 'none' }}>
            <Form.Item label="对方账户" colon={false}>
              {getFieldDecorator(`otherAccount`, {
                rules: [{}],
              })(<Input placeholder="请输入" />)}
            </Form.Item>
          </Col>
          <Col span={6} style={{ display: expand ? 'block' : 'none' }}>
            <Form.Item label="生成单据标志" colon={false}>
              {getFieldDecorator(`isGenerate`, {
                rules: [{}],
              })(
                <Select placeholder="前台写死（待生成、已生成）">
                  <Option value="false">待生成</Option>
                  <Option value="true">已生成</Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col span={3} style={{ display: expand ? 'block' : 'none' }}>
            <Form.Item label="发生金额从" colon={false}>
              {getFieldDecorator(`amountFrom`, {
                rules: [{}],
              })(<Input placeholder="请输入" />)}
            </Form.Item>
          </Col>
          <Col span={3} style={{ display: expand ? 'block' : 'none' }}>
            <Form.Item label="发生金额至" colon={false}>
              {getFieldDecorator(`amountTo`, {
                rules: [{}],
              })(<Input placeholder="请输入" onBlur={this.checkMoney} />)}
            </Form.Item>
          </Col>
          <Col span={6} style={{ display: expand ? 'block' : 'none' }}>
            <Form.Item label="借贷方向" colon={false}>
              {getFieldDecorator(`direction`, {
                rules: [{}],
              })(
                <Select placeholder="前台写死（借方、贷方）">
                  <Option value="D">借方</Option>
                  <Option value="C">贷方</Option>
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={24} style={{ textAlign: 'right' }}>
            <a style={{ marginRight: 15, fontSize: 16 }} onClick={this.toggle} className="more">
              更多 <Icon type={expand ? 'up' : 'down'} />
            </a>
            <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
              搜索
            </Button>
            <Button style={{ marginLeft: 8 }} onClick={this.handleReset}>
              清空
            </Button>
          </Col>
        </Row>
      </Form>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(AccountStatementSearch));
