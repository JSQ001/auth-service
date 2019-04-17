import React from 'react';
import { Form, Button, Row, Col, Input } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;

class CashierReconciliationSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 搜索
   */
  handleSearch = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
      submitHandle,
    } = this.props;
    const params = getFieldsValue();
    submitHandle(params);
  };

  /**
   * 清空
   */
  handleClear = () => {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
    } = this.props;
    return (
      <div className="train">
        <div className="common-top-area">
          {/* <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={5}
            />
          </Row> */}
          <Form>
            <Row>
              <Col span={4}>
                <FormItem label="公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('company', {
                    initialValue: company.name,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col span={4} offset={1}>
                <FormItem label="银行账号:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('bankNumber', {
                    initialValue: '',
                  })(<Input placeholder="请选择" />)}
                </FormItem>
              </Col>
              <Col span={4} offset={1}>
                <FormItem label="币种" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('currency', {
                    initialValue: '',
                  })(<Input placeholder={company.name} />)}
                </FormItem>
              </Col>
              <Col span={4} offset={1}>
                <FormItem label="期间" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('period', {
                    initialValue: '',
                  })(<Input placeholder="请选择" />)}
                </FormItem>
              </Col>
              <Col span={4} offset={1}>
                <FormItem label="对账状态" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('reconciliationStatus', {
                    initialValue: '',
                  })(<Input placeholder={company.name} />)}
                </FormItem>
              </Col>
            </Row>
            <Row style={{ marginTop: '20px' }}>
              <Col offset={20}>
                <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                  搜索
                </Button>
                <Button style={{ marginLeft: 30 }} onClick={this.handleClear}>
                  清空
                </Button>
              </Col>
            </Row>
          </Form>
        </div>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
export default connect(map)(Form.create()(CashierReconciliationSearch));
