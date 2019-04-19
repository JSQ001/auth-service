import React from 'react';
import { connect } from 'dva';
import config from 'config';
import httpFetch from 'share/httpFetch';
import {
  Form,
  Row,
  Col,
  Popover,
  Input,
  Button,
  Tag,
  message,
  Spin,
  Dropdown,
  Menu,
  Icon,
} from 'antd';
import { routerRedux } from 'dva/router';
import PropTypes from 'prop-types';
import 'styles/components/template/approve-bar.scss';
import 'styles/reimburse/reimburse-common.scss';
import Addtional from 'components/Widget/Template/approve-bar-detail/addtional';
import Bondage from 'components/Widget/Template/approve-bar-detail/bondage';
import Notify from 'components/Widget/Template/approve-bar-detail/notify';
import Back from 'components/Widget/Template/approve-bar-detail/return-back';
const FormItem = Form.Item;
const { CheckableTag } = Tag;

class ApproveBar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      tags: [],
      fastReplyEdit: false,
      inputVisible: false,
      inputValue: '',
      fastReplyChosen: [],
      inputError: false,
      errorMessage: '',
      passLoading: false,
      rejectLoading: false,
      moreButtonTag: '',
      moreButtonFlag: false,
    };
  }

  componentWillMount() {
    this.getQuickTags();
  }

  //获取快捷回复内容
  getQuickTags = () => {
    this.setState({ loading: true });
    let url = '/api/quick/reply';
    if (this.props.audit) {
      url = `/api/dudit/quick/reply?userOid=${this.props.user.userOid}`;
    }
    httpFetch.get(`${config.workflowUrl}${url}`).then(res => {
      if (res.status === 200) {
        let tags = this.props.audit ? res.data.rows : res.data;
        this.setState({ tags: tags, loading: false });
      }
    });
  };

  //显示新增快捷回复输入框
  showTagInput = () => {
    this.setState({ inputVisible: true }, () => this.input.focus());
  };

  //选择快捷回复
  onFastReplyChange = (checked, id) => {
    const { getFieldsValue, setFieldsValue } = this.props.form;
    let { tags, fastReplyChosen } = this.state;
    let fastReplyChosenValue = [];
    if (getFieldsValue().reason) {
      fastReplyChosenValue.push(getFieldsValue().reason);
      fastReplyChosenValue = fastReplyChosenValue[0].split('，');
    }
    tags.map(item => {
      if (item.id === id) {
        item.checked = true;
        fastReplyChosen.push(item);
        fastReplyChosenValue.push(item.reply);
      }
    });
    setFieldsValue({ reason: fastReplyChosenValue.join('，') });
    this.setState(
      {
        fastReplyChosen,
        inputError: fastReplyChosenValue.join('，').length > 200,
        errorMessage: this.$t('common.max.characters.length', { max: 200 }),
      },
      () => {
        this.reasonInput.focus();
      }
    );
  };

  //审批意见输入
  onReasonChange = e => {
    this.setState({
      inputError: e.target.value.length > 200,
      errorMessage: this.$t('common.max.characters.length', { max: 200 }),
    });
  };

  onInputChange = e => {
    this.setState({ inputValue: e.target.value });
  };

  //确认新增的快捷回复
  handleInputConfirm = () => {
    const { inputValue } = this.state;
    if (inputValue && inputValue.trim() && inputValue.trim().length <= 200) {
      this.setState({ loading: true });
      let url = '/api/quick/reply';
      let param = { reply: inputValue.trim() };
      if (this.props.audit) {
        url = '/api/dudit/quick/reply';
        param = {
          reply: inputValue.trim(),
          tenantId: this.props.company.tenantId,
        };
      }
      httpFetch.post(`${config.workflowUrl}${url}`, param).then(res => {
        if (res.status === 200) {
          this.setState({ inputVisible: false, inputValue: '' });
          this.getQuickTags();
          message.success(this.$t('common.operate.success') /*操作成功*/);
        }
      });
    } else {
      this.setState({ inputVisible: false });
    }
  };

  //删除快捷回复标签
  handleDeleteTag = (e, item) => {
    e.stopPropagation();
    this.setState({ loading: true });
    let url = '/api/quick/reply';
    let param = { quickReplyOids: item.quickReplyOid };
    if (this.props.audit) {
      url = '/api/dudit/quick/reply';
      param = { id: item.id };
    }
    httpFetch
      .delete(`${config.workflowUrl}${url}?quickReplyOids=${item.quickReplyOid}`)
      .then(res => {
        let isSuccess = this.props.audit ? res.data.rows : true;
        if (res.status === 200 && isSuccess) {
          this.getQuickTags();
          message.success(this.$t('common.delete.success', { name: '' }));
        } else {
          this.setState({ loading: false });
          message.error(this.$t('common.operate.filed')); //操作失败
        }
      })
      .catch(e => {
        this.setState({ loading: false });
        message.success(`${this.$t('common.operate.filed')}，${e.response.data.message}`);
      });
  };

  handleFastReplyEdit = () => {
    let fastReplyEdit = this.state.fastReplyEdit;
    this.setState({ fastReplyEdit: !fastReplyEdit });
  };

  //审批通过
  handleApprovePass = () => {
    let method = this.props.handleApprovePass;
    let values = this.props.form.getFieldsValue();
    if (values.reason && values.reason.length > 200) return;

    const { documentType, documentOid } = this.props;
    let params = {
      approvalTxt: values.reason,
      entities: [
        {
          entityOid: documentOid,
          entityType: documentType,
        },
      ],
      countersignApproverOids: [],
    };

    this.setState({ passLoading: true });
    method
      ? method(values.reason)
      : httpFetch
          .post(`${config.workflowUrl}/api/workflow/pass`, params)
          .then(res => {
            if (res.data.failNum === 0) {
              message.success('操作成功');
              this.setState({ passLoading: false });
              this.goBack();
            } else {
              message.success('操作失败：' + JSON.stringify(res.data.failReason));
              this.setState({ passLoading: false });
            }
          })
          .catch(e => {
            this.setState({ passLoading: false });
            message.error(`操作失败，${e.response.data.message}`);
          });
  };

  //审批驳回
  handleApproveReject = () => {
    let method = this.props.handleApproveReject;

    let values = this.props.form.getFieldsValue();
    if (values.reason && values.reason.length > 200) return;
    if (!values.reason || !values.reason.trim()) {
      this.setState({
        inputError: true,
        errorMessage: this.$t('approve-bar.please.enter.reject'),
      });
    } else {
      const { documentType, documentOid } = this.props;

      let model = {
        approvalTxt: values.reason,
        entities: [
          {
            entityOid: documentOid,
            entityType: documentType,
          },
        ],
      };
      this.setState({ inputError: false, rejectLoading: true });
      method
        ? method(values.reason)
        : httpFetch
            .post(`${config.workflowUrl}/api/workflow/reject`, model)
            .then(res => {
              if (res.status === 200) {
                this.setState({ rejectLoading: false });
                message.success('操作成功！');
                this.goBack();
              } else {
                this.setState({ rejectLoading: false });
                message.error(`提交失败，${e.response.data.message}`);
              }
            })
            .catch(e => {
              this.setState({ rejectLoading: false });
              message.error(`提交失败，${e.response.data.message}`);
            });
    }
  };

  //返回
  goBack = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: this.props.backUrl,
      })
    );
  };

  moreButtonClick = e => {
    this.setState({ moreButtonTag: e.key, moreButtonFlag: true });
  };

  moreButtonCancel = () => {
    this.setState({ moreButtonFlag: false });
  };
  renderMoreInfo = () => {
    const { moreButtonTag, moreButtonFlag } = this.state;
    switch (moreButtonTag) {
      case 'addtional':
        return (
          <Addtional
            modalVisible={moreButtonFlag}
            onClose={this.moreButtonCancel}
            documentType={this.props.documentType}
            documentOid={this.props.documentOid}
          />
        );
      case 'bondage':
        return (
          <Bondage
            modalVisible={moreButtonFlag}
            onClose={this.moreButtonCancel}
            documentType={this.props.documentType}
            documentOid={this.props.documentOid}
          />
        );
      case 'notify':
        return (
          <Notify
            modalVisible={moreButtonFlag}
            onClose={this.moreButtonCancel}
            documentType={this.props.documentType}
            documentOid={this.props.documentOid}
          />
        );
      case 'back':
        return (
          <Back
            modalVisible={moreButtonFlag}
            onClose={this.moreButtonCancel}
            documentType={this.props.documentType}
            documentOid={this.props.documentOid}
          />
        );
    }
  };
  render() {
    const { getFieldDecorator } = this.props.form;
    const { audit, width, style } = this.props;
    const {
      loading,
      tags,
      fastReplyEdit,
      inputVisible,
      inputValue,
      inputError,
      errorMessage,
      passLoading,
      rejectLoading,
      moreButtonFlag,
    } = this.state;
    const fastReplyTitle = (
      <div className="fast-reply-title">
        {this.$t('approve-bar.quick.reply') /*快捷回复*/}
        {!fastReplyEdit && (
          <a className="edit" onClick={this.handleFastReplyEdit}>
            {this.$t('common.edit') /*编辑*/}
          </a>
        )}
        {fastReplyEdit && (
          <a className="edit" onClick={this.handleFastReplyEdit}>
            {this.$t('common.cancel') /*取消*/}
          </a>
        )}
      </div>
    );
    const fastReplyContent = (
      <div className="fast-reply">
        <Spin spinning={loading}>
          {tags.map(item => {
            return (
              <CheckableTag
                key={item.id}
                className="fast-reply-tag"
                onChange={checked => this.onFastReplyChange(checked, item.id)}
              >
                {item.reply}
                {fastReplyEdit && (
                  <a className="delete-tag" onClick={e => this.handleDeleteTag(e, item)}>
                    &times;
                  </a>
                )}
              </CheckableTag>
            );
          })}
          {!inputVisible &&
            !fastReplyEdit && (
              <Button
                size="small"
                type="dashed"
                className="add-new-btn"
                onClick={this.showTagInput}
              >
                + {this.$t('approve-bar.new.quick.reply') /*新增快速回复*/}
              </Button>
            )}
          {inputVisible && (
            <Input
              ref={input => (this.input = input)}
              type="text"
              size="small"
              className="fast-reply-input"
              value={inputValue}
              onChange={this.onInputChange}
              onBlur={this.handleInputConfirm}
              onPressEnter={this.handleInputConfirm}
            />
          )}
        </Spin>
      </div>
    );
    const barLayout = {
      lg: 12,
      md: 24,
      sm: 24,
      xs: 24,
    };
    let menu = (
      <Menu onClick={this.moreButtonClick}>
        <Menu.Item key="addtional">加签</Menu.Item>
        <Menu.Item key="bondage">转交</Menu.Item>
        <Menu.Item key="back">退回指定节点</Menu.Item>
        <Menu.Item key="notify">通知</Menu.Item>
      </Menu>
    );
    return (
      <div className="approve-bar" style={{ width, ...style }}>
        <Row>
          <Col {...barLayout}>
            <Form layout={'inline'}>
              <FormItem
                label={
                  audit
                    ? this.$t('approve-bar.audit.suggest')
                    : this.$t('approve-bar.approve.suggest')
                }
                validateStatus={inputError ? 'error' : ''}
                help={inputError ? errorMessage : ''}
                className="approve-form"
              >
                <Popover
                  trigger="click"
                  title={fastReplyTitle}
                  content={fastReplyContent}
                  getPopupContainer={() => document.getElementsByClassName('approve-bar')[0]}
                  overlayStyle={{ width: '69%', maxHeight: '140px' }}
                >
                  {getFieldDecorator('reason')(
                    <Input
                      placeholder={this.$t('common.max.characters.length', { max: 200 })}
                      ref={node => (this.reasonInput = node)}
                      onChange={this.onReasonChange}
                    />
                  )}
                </Popover>
              </FormItem>
            </Form>
          </Col>
          <Col {...barLayout} className="approve-btn" style={{ paddingRight: '40px' }}>
            <span>
              <Button
                type="primary"
                onClick={this.handleApprovePass}
                loading={passLoading}
                htmlType="submit"
                className="pass-btn"
              >
                {this.$t('common.pass')}
              </Button>
              <Button
                loading={rejectLoading}
                onClick={this.handleApproveReject}
                htmlType="submit"
                className="reject-btn"
              >
                {this.$t('common.reject')}
              </Button>
              {this.props.backUrl && (
                <Button className="back-btn" htmlType="submit" onClick={this.goBack}>
                  {this.$t('common.back')}
                </Button>
              )}
              <Dropdown overlay={menu}>
                <Button style={{ marginLeft: 8 }}>
                  更多操作 <Icon type="down" />
                </Button>
              </Dropdown>
            </span>
          </Col>
        </Row>
        {moreButtonFlag && this.renderMoreInfo()}
      </div>
    );
  }
}

ApproveBar.propTypes = {
  documentType: PropTypes.number, //单据类型
  documentOid: PropTypes.string, //单据oid
  backUrl: PropTypes.string, //点击"返回"按钮跳转到的页面
  passLoading: PropTypes.bool, // 通过按钮loading
  rejectLoading: PropTypes.bool, // 驳回按钮loading
};

ApproveBar.defaultProps = {
  passLoading: false,
  rejectLoading: false,
};

const wrappedApproveBar = Form.create(mapStateToProps)(ApproveBar);

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedApproveBar);
