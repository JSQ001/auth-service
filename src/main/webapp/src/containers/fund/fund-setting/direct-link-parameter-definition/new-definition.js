import React from 'react';
import { Form, Row, Col, Input, Switch, Button } from 'antd';
import { connect } from 'dva';
import Lov from 'widget/Template/lov';
import service from './direct-link-parameter-definition.service';

const FormItem = Form.Item;

class NewDefinition extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      bankCode: '',
    };
  }

  /**
   * 选择银行
   */
  selectBank = value => {
    const { form } = this.props;
    form.resetFields(['bankName', 'bankCode']);
    this.setState({
      bankCode: value.openBank,
    });
  };

  /**
   * 保存
   */
  save = e => {
    const { form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (!err) {
        const record = {
          bankCode: values.bankCode,
          bankName: values.bankName.openBankName,
          description: values.directDescription,
          enabled: values.status ? 1 : 0,
        };
        service.insertHead(record);
      }
    });
  };

  /**
   * 取消
   */
  cancel = () => {
    const { onClose } = this.props;
    onClose();
  };

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    const { bankCode } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };
    return (
      <div>
        <Row>
          <Col style={{ borderBottom: '1px solid #e9e9e9', marginBottom: '20px' }}>
            <h1>基本信息</h1>
          </Col>
        </Row>
        <Form>
          <FormItem label="银行名称" {...formItemLayout}>
            {getFieldDecorator('bankName', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: '',
            })(
              <Lov
                code="bank_choose"
                labelKey="accountName"
                valueKey="id"
                onChange={value => this.selectBank(value)}
                single
                allowClear
              />
            )}
          </FormItem>
          <FormItem label="银行代码" {...formItemLayout}>
            {getFieldDecorator('bankCode', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: bankCode,
            })(<Input disabled />)}
          </FormItem>
          <FormItem label="直联描述" {...formItemLayout}>
            {getFieldDecorator('directDescription', {
              rules: [{ required: false, message: this.$t('common.please.select') }],
              initialValue: '',
            })(<Input />)}
          </FormItem>
          <FormItem label="状态" {...formItemLayout}>
            {getFieldDecorator('status', {
              rules: [{ required: false, message: this.$t('common.please.select') }],
              initialValue: false,
            })(<Switch />)}
          </FormItem>
        </Form>
        <Row>
          <Col span={24} align="center">
            <Button type="primary" style={{ marginRight: '10px' }} onClick={e => this.save(e)}>
              保存
            </Button>
            <Button
              style={{ marginLeft: '10px' }}
              onClick={() => {
                this.cancel();
              }}
            >
              取消
            </Button>
          </Col>
        </Row>
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

export default connect(map)(Form.create()(NewDefinition));
