import React, { Component } from 'react';
import { Form, Row, Col, Input, Button } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import PropTypes from 'prop-types';
import 'styles/exp-input-tax/approval-bar-dwy.scss';

const FormItem = Form.Item;

class ApprovalBarDWY extends Component {
  constructor(props) {
    super(props);
    this.state = {
      inputError: false,
      errorMsg: '',
    };
  }

  // 校验input value length
  getRemarkLength = e => {
    this.setState({
      inputError: e.target.value.length > 200,
      errorMsg: this.$t('common.max.characters.length', { max: 200 }),
    });
  };

  handlePass = e => {
    e.preventDefault();
    const { onPass, form } = this.props;
    const { inputError } = this.state;
    const { validateFields } = form;
    validateFields((err, value) => {
      if (err) return;
      console.log(value);
      onPass(inputError, value);
    });
  };

  handleRefuse = () => {
    const { onRefuse, form } = this.props;
    const { inputError } = this.state;
    const { validateFields } = form;
    validateFields((err, value) => {
      if (err) return;
      console.log(value);
      if (!value.approvalRemark || !value.approvalRemark.trim()) {
        this.setState({
          inputError: true,
          errorMsg: this.$t('approve-bar.please.enter.reject'),
        });
      } else onRefuse(inputError, value);
    });
  };

  // 返回至
  handleBack = () => {
    const { dispatch, backUrl } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: backUrl,
      })
    );
  };

  render() {
    const { form, flag, passLoading, refuseLoading } = this.props;
    const { getFieldDecorator } = form;
    const { inputError, errorMsg } = this.state;

    return (
      <div className="tax-approval-bar-dwy">
        {flag === 'Y' ? (
          <Row span={24}>
            <Col span={10}>
              <Form layout="inline">
                <FormItem
                  label={this.$t('approve-bar.audit.suggest')}
                  validateStatus={inputError ? 'error' : ''}
                  help={inputError ? errorMsg : ''}
                  style={{ width: '100%' }}
                  labelCol={{ span: 6 }}
                  wrapperCol={{ span: 18 }}
                >
                  {getFieldDecorator('approvalRemark', {})(
                    <Input
                      placeholder={this.$t('common.max.characters.length', { max: 200 })}
                      onChange={this.getRemarkLength}
                      style={{ width: '100%' }}
                    />
                  )}
                </FormItem>
              </Form>
            </Col>
            <Col span={14} className="bar-btn">
              <Button
                type="primary"
                loading={passLoading}
                htmlType="submit"
                onClick={this.handlePass}
              >
                {this.$t('common.pass')}
              </Button>
              <Button
                loading={refuseLoading}
                className="refuse-btn"
                type="danger"
                onClick={this.handleRefuse}
              >
                {/* 驳回 */}
                {this.$t('common.reject')}
              </Button>
              <Button onClick={this.handleBack}>{this.$t('common.return')}</Button>
            </Col>
          </Row>
        ) : (
          <Row>
            <Col span={10} push={1}>
              <Button onClick={this.handleBack}>{this.$t('common.return')}</Button>
            </Col>
          </Row>
        )}
      </div>
    );
  }
}

ApprovalBarDWY.propTypes = {
  passLoading: PropTypes.bool,
  refuseLoading: PropTypes.bool,
  backUrl: PropTypes.string, // 点击"返回"按钮跳转到的页面
  onPass: PropTypes.func, // 通过事件
  onRefuse: PropTypes.func, // 驳回事件
  flag: PropTypes.string, // Y / N 组件为返回功能的btn或是一组按钮及form
};

ApprovalBarDWY.defaultProps = {
  passLoading: false,
  refuseLoading: false,
  backUrl: '',
  onPass: () => {},
  onRefuse: () => {},
  flag: 'Y',
};

const wrappedApproveBar = Form.create()(ApprovalBarDWY);
export default connect()(wrappedApproveBar);
