import React from 'react';
import { connect } from 'dva';
import { Form, Switch, Icon, Input, Radio, Button } from 'antd';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class AutoCashierReconciliation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: '1',
    };
  }

  /**
   * 对账选择
   */
  onChange = e => {
    this.setState({
      value: e.target.value,
    });
  };

  /**
   * 保存
   */
  handleSave = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
    } = this.props;
    const params = getFieldsValue();
    /* disable-eslint */
    console.log(params);
    /* enable-eslint */
  };

  /**
   * 取消
   */
  handleClose = () => {
    const { onClose } = this.props;
    onClose();
  };

  render() {
    const {
      form: { getFieldDecorator },
      // user,
      // company,
    } = this.props;
    const formItemLayout1 = {
      labelCol: {
        span: 10,
      },
      wrapperCol: {
        span: 6,
      },
    };
    const { value } = this.state;

    return (
      <div>
        <Form>
          <FormItem label="对账类型" {...formItemLayout1}>
            {getFieldDecorator('reconciliationType', {
              // rules: [{ required: true }],
              initialValue: '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(
              <RadioGroup onChange={this.onChange} defaultValue={value} value={value}>
                <Radio defaultChecked value="1">
                  精准
                </Radio>
                <Radio value="2">模糊</Radio>
              </RadioGroup>
            )}
          </FormItem>
          <FormItem label="摘要" {...formItemLayout1}>
            {getFieldDecorator('abstract', {
              // rules: [{ required: true }],
              valuePropName: 'checked',
              initialValue: '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(
              <Switch
                style={{ paddingLeft: '10px', marginLeft: '30px' }}
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                disabled={value !== '2'}
              />
            )}
          </FormItem>
          <FormItem label="对方户名" {...formItemLayout1}>
            {getFieldDecorator('otherName', {
              // rules: [{ required: true }],
              valuePropName: 'checked',
              initialValue: '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(
              <Switch
                style={{ paddingLeft: '10px', marginLeft: '30px' }}
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                disabled={value !== '2'}
              />
            )}
          </FormItem>
          <FormItem label="范围" {...formItemLayout1}>
            {getFieldDecorator('range', {
              // rules: [{ required: true }],
              valuePropName: 'checked',
              initialValue: '',
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(
              <Switch
                style={{ paddingLeft: '10px', marginLeft: '30px' }}
                checkedChildren={<Icon type="check" />}
                unCheckedChildren={<Icon type="cross" />}
                disabled={value !== '2'}
              />
            )}
          </FormItem>
          <FormItem label="天数" {...formItemLayout1}>
            {getFieldDecorator('dayTime', {
              // rules: [{ required: true }],
              // initialValue: false,
              // initialValue: typeof updateDateU.id === 'undefined' ? true : updateDateU.queryFlag,
            })(<Input AUTOCOMPLETE="off" disabled={value !== '2'} />)}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
            >
              保存
            </Button>
            <Button onClick={this.handleClose}>取消</Button>
          </div>
        </Form>
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
export default connect(map)(Form.create()(AutoCashierReconciliation));
