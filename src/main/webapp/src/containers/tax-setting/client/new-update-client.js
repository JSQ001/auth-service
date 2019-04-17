import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import {
  Form,
  Button,
  Input,
  Card,
  Row,
  Col,
  Select,
  InputNumber,
  DatePicker,
  message,
  Tag,
  Modal,
} from 'antd';
const FormItem = Form.Item;
const Option = Select.Option;
const { TextArea } = Input;
import moment from 'moment';
import config from 'config';
import httpFetch from 'share/httpFetch';
import clientService from './service';
import Chooser from 'widget/chooser';
import Upload from 'widget/upload-button';
import clientAppOtherDetail from './client-other';
import ApproveHistory from 'widget/Template/approve-history-work-flow';
class NewUpdateClient extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      queryFlag: true,
      taxClientTypeOptions: [],
      taxCredentialsTypeOptions: [],
      taxQuaifationOptions: [],
      taxTransactionTypeOptions: [
        { value: 'TAX_CLIENT_NEW', label: '客户信息新增' },
        { value: 'TAX_CLIENT_EDIT', label: '客户信息修改' },
      ],
      taxTransactionStatusOptions: [
        { value: 'GENERATE', label: '编辑中' },
        { value: 'APPROVAL_PASS', label: '审批通过' },
        { value: 'APPROVAL', label: '审批中' },
        { value: 'APPROVAL_REJECT', label: '审批驳回' },
        { value: 'WITHDRAW', label: '撤回' },
      ],
      currencyList: [], //币种
      data: {},
      abledRefundAmount: 0,
      showDetail: false,
      detailFlag: '',
      applicationId: undefined,
      saveData: {}, //保存后返回新的支付数据
      saveFlag: true, //是否已经保存
      btnLoading: false, //按钮加载
      disabled: false,
      fileList: [], // 附件
      uploadOids: [], //附件OidS
      fileShow: false,
      clientSelectorItem: {
        title: '请选择客户',
        url: `${config.taxUrl}/api/tax/client/query/condition`,
        searchForm: [
          { type: 'input', id: 'clientNumber', label: '客户编码' },
          { type: 'input', id: 'clientName', label: '客户名称' },
        ],
        columns: [
          { title: '客户编码', dataIndex: 'clientNumber', width: 150 },
          { title: '客户名称', dataIndex: 'clientName', width: 150 },
        ],
        key: 'clientNumber',
      },
    };
  }

  /**
   * 客户类型下拉列表
   */
  getClientType() {
    // eslint-disable-next-line prefer-const
    let taxClientTypeOptions = [];
    this.getSystemValueList('TAX_CLIENT_TYPE').then(res => {
      res.data.values.map(data => {
        taxClientTypeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxClientTypeOptions,
      });
    });
  }

  /**
   * 证件类型下拉列表
   */
  getCredentialsType() {
    // eslint-disable-next-line prefer-const
    let taxCredentialsTypeOptions = [];
    this.getSystemValueList('1006').then(res => {
      res.data.values.map(data => {
        taxCredentialsTypeOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxCredentialsTypeOptions,
      });
    });
  }

  /**
   * 纳税资质下拉列表
   */
  getTaxQuaifation() {
    // eslint-disable-next-line prefer-const
    let taxQuaifationOptions = [];
    this.getSystemValueList('TAX_QUAIFICATION').then(res => {
      res.data.values.map(data => {
        taxQuaifationOptions.push({
          label: data.messageKey,
          value: data.value,
          key: data.value,
        });
      });
      this.setState({
        taxQuaifationOptions,
      });
    });
  }

  componentWillMount() {
    this.getCurrencyList();
    this.getClientType();
    this.getCredentialsType();
    this.getTaxQuaifation();
  }
  //获取币种列表
  getCurrencyList = () => {
    if (this.state.currencyList.length === 0) {
      httpFetch
        .get(`${config.mdataUrl}/api/currency/rate/company/standard/currency/getAll`)
        .then(res => {
          this.setState({ currencyList: res.data });
        });
    }
  };

  //获取收款账户 即原付款公司
  getPayAccount = (paymentCompanyId, currency) => {
    if (this.state.draweeAccountNumber.length > 0) return;
    let url = `${
      config.payUrl
    }/api/CompanyBank/selectByCompanyId?companyId=${paymentCompanyId}&currency=${currency}`;
    httpFetch
      .get(url)
      .then(res => {
        res.status === 200 && this.setState({ draweeAccountNumber: res.data || [] });
      })
      .catch(() => {
        message.error(
          this.$t({ id: 'pay.refund.getDraweeAccountNumberError' } /*获取收款方银行账户信息失败*/)
        );
      });
  };

  //获取退款账户 即原员工或供应商
  getAccount = name => {
    if (this.state.payeeAccountNumber.length > 0) return;
    let url = `${
      config.baseUrl
    }/api/expReportHeader/get/bank/info/by/name?name=${name}&empFlag=1003`;
    httpFetch
      .get(url)
      .then(res => {
        if (res.status === 200 && res.data[0]) {
          this.setState({ payeeAccountNumber: res.data[0].bankInfos || [] });
        }
      })
      .catch(e => {
        message.error(
          this.$t({ id: 'pay.refund.getPayeeAccountNumberError' } /*获取退款方银行账户信息失败*/)
        );
      });
  };

  componentDidMount() {
    this.setState({ formLoading: true });
    let data = {};

    // data = record || {};
    if (this.props.params.record.id === undefined) {
      data = this.props.params.record;
      this.setData(data);
    } else {
      clientService
        .getApplicationDetail(this.props.params.record.id)
        .then(res => {
          data = res.data;
          this.setData(data);
        })
        .catch(err => {
          console.error(err);
          message.error(err.response.data.message);
          this.setState({ getLoading: false });
        });
    }
  }

  setData = data => {
    this.setState({
      queryFlag: false,
      data: data,
      saveData: data,
      fileShow: true,
      disabled: !(
        (data.transactionStatus && data.transactionStatus === 'GENERATE') ||
        data.transactionStatus === 'APPROVAL_REJECT' ||
        data.transactionStatus === 'WITHDRAW'
      ),
    });
    let clientNumber = data.clientNumber === undefined ? [] : [{ clientNumber: data.clientNumber }];
    let values = this.props.form.getFieldsValue();
    let result = {};
    for (let name in values) {
      if (name === 'clientNumberList') {
        result[name] = clientNumber;
      } else {
        result[name] = data[name];
      }
    }
    this.props.form.setFieldsValue(result);
  };

  onCancel = () => {
    this.setState({ loading: false }, () => {
      this.props.onClose(true);
    });
  };
  //校验可退款金额
  checkAmount = (rule, value, callback) => {
    if (value && value > this.state.abledRefundAmount) {
      callback(
        this.$t({ id: 'pay.refund.amountGTabledAmount' } /*输入金额大于可退款金额*/) +
          this.state.abledRefundAmount
      );
    } else if (value <= 0) {
      callback(this.$t({ id: 'pay.refund.amountLTZero' } /*可退款金额必须大于0*/));
    } else {
      callback();
    }
  };

  //查看支付流水详情
  viewPayDetail = id => {
    this.setState({
      showDetail: true,
      detailId: id,
      detailFlag: 'PAYDETAIL',
    });
  };

  //查看单据详情
  viewDocumentDetail = (id, documentCategory) => {
    this.setState({
      showDetail: true,
      detailId: id,
      detailFlag: documentCategory,
    });
  };

  //弹出框关闭
  onClose = () => {
    this.setState({
      showDetail: false,
    });
  };

  /**
   * 组装方法
   * @param content 内部组件
   * @return {*} 给组件添加this.props.close(params)方法,params为返回到最外层的值
   *             同时添加外部传入的props为内部组件可用
   */
  wrapClose = content => {
    let applicationId = this.state.applicationId;
    const newProps = {
      params: { applicationId: applicationId, flag: this.state.disabled },
    };
    return React.createElement(content, Object.assign({}, newProps.params, newProps));
  };

  saveFunction = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      const { uploadOids } = this.state;
      let backlashAttachmentOid = uploadOids.join(',');
      if (!err) {
        //新建
        if (!this.state.saveData.id) {
          this.setState({ btnLoading: true });
          let params = { ...this.state.data, ...values };
          params['backFlashAttachmentOids'] = backlashAttachmentOid;
          params['clientNumber'] =
            params.clientNumberList.length === 0 ? null : params.clientNumberList[0].clientNumber;
          clientService
            .saveFunction(params)
            .then(res => {
              if (res.status === 200) {
                this.setState({
                  queryFlag: false,
                  saveData: res.data || {},
                  saveFlag: true,
                  btnLoading: false,
                });
                message.success(
                  this.$t({ id: 'common.save.success' }, { name: '' } /*保存成功*/) + '!'
                );
                let result = {
                  applicationCode: res.data.applicationCode,
                  transactionStatus: res.data.transactionStatus,
                  transactionType: res.data.transactionType,
                  clientNumberList:
                    res.data.clientNumber === undefined
                      ? []
                      : [{ clientNumber: res.data.clientNumber }],
                };
                this.props.form.setFieldsValue(result);
              }
            })
            .catch(e => {
              this.setState({
                btnLoading: false,
              });
              message.error(
                this.$t({ id: 'common.save.filed' } /*保存失败*/) + '!' + e.response.data.message
              );
            });
        } else {
          //修改
          this.setState({ btnLoading: true });
          let params = { ...this.state.saveData, ...values };
          params.versionNumber = this.props.params.record.versionNumber;
          params.id = this.state.saveData.id;
          params['backFlashAttachmentOids'] = backlashAttachmentOid;
          params['clientNumber'] =
            params.clientNumberList.length === 0 ? null : params.clientNumberList[0].clientNumber;
          clientService
            .updateClientData(params)
            .then(res => {
              if (res.status === 200) {
                this.setState({
                  queryFlag: false,
                  saveData: res.data || {},
                  saveFlag: true,
                  btnLoading: false,
                });
                message.success(
                  this.$t({ id: 'common.save.success' }, { name: '' } /*保存成功*/) + '!'
                );
              }
            })
            .catch(e => {
              this.setState({
                btnLoading: false,
              });
              message.error(
                this.$t({ id: 'common.save.filed' } /*保存失败*/) + '!' + e.response.data.message
              );
            });
        }
      }
    });
  };
  //点击通知财务退款
  onSubmit = () => {
    if (this.state.saveFlag === false) {
      Modal.warning({
        title: '提示',
        content: '请先保存！',
      });
    } else {
      const e = this;
      let params = {
        applicantOid: this.state.saveData.applicationOid,
        formOid: this.state.saveData.formOid,
        documentOid: this.state.saveData.documentOid,
        userOid: this.state.saveData.applicationOid,
        documentCategory: 904001,
        countersignApproverOIDs: null,
        documentNumber: this.state.saveData.applicationCode,
        companyId: this.state.saveData.companyId,
        unitOid: null,
        amount: 0,
        functionAmount: 0,
        currencyCode: '',
        documentId: this.state.saveData.id,
      };
      Modal.confirm({
        title: '是否确认提交？',
        //content: this.$t({ id: "pay.refund.inform" }/*通知后需要进行复核*/),
        okText: this.$t({ id: 'common.ok' } /*确定*/),
        cancelText: this.$t({ id: 'common.cancel' } /*取消*/),
        onOk() {
          e.setState({ btnLoading: true });
          clientService
            .submit(params)
            .then(res => {
              message.success('提交成功！');
              e.setState({ btnLoading: false });
              e.onCancel();
            })
            .catch(err => {
              message.error(err.response.data.message);
              e.setState({ btnLoading: false });
            });
        },
        onCancel() {},
      });
    }
  };
  //点击删除
  onDelete = () => {
    const e = this;
    Modal.confirm({
      title: '确认删除这条数据?',
      // content: this.$t({ id: "pay.refund.deleteInfo" }/*删除后可以重新添加退款信息!*/),
      okText: this.$t({ id: 'common.ok' } /*确定*/),
      cancelText: this.$t({ id: 'common.cancel' } /*取消*/),
      onOk() {
        e.setState({ btnLoading: true });
        clientService
          .deleteClientApplication(e.state.saveData.id)
          .then(res => {
            if (res.status === 200) {
              e.setState({ btnLoading: false });
              e.onCancel();
              message.success(e.$t({ id: 'common.operate.success' } /*操作成功*/) + '!');
            }
          })
          .catch(err => {
            e.setState({ btnLoading: false });
            message.error(
              e.$t({ id: 'common.operate.filed' } /*操作失败*/) + '!' + err.response.data.message
            );
          });
      },
      onCancel() {},
    });
  };
  //修改客户编码
  clientNumberChange = value => {
    if (value.length !== 0 && value[0].id !== undefined) {
      this.props.form.setFieldsValue({
        clientName: value[0].clientName,
        clientType: value[0].clientType,
        credentialsType: value[0].credentialsType,
        credentialsNumber: value[0].credentialsNumber,
        contacts: value[0].contacts,
        contactsPhone: value[0].contactsPhone,
        contactsAddress: value[0].contactsAddress,
        taxpayerName: value[0].taxpayerName,
        taxpayerNumber: value[0].taxpayerNumber,
        taxQualification: value[0].taxQualification,
        address: value[0].address,
        phone: value[0].phone,
        openingBank: value[0].openingBank,
        accountNumber: value[0].accountNumber,
        email: value[0].email,
        cellphone: value[0].cellphone,
        mailAddress: value[0].mailAddress,
        addressEE: value[0].addressEE,
        addressEEPhone: value[0].addressEEPhone,
      });
    } else if (value.length !== 0 && value[0].id === undefined) {
    } else {
      this.props.form.setFieldsValue({
        clientName: null,
        clientType: null,
        credentialsType: null,
        credentialsNumber: null,
        contacts: null,
        contactsPhone: null,
        contactsAddress: null,
        taxpayerName: null,
        taxpayerNumber: null,
        taxQualification: null,
        address: null,
        phone: null,
        openingBank: null,
        accountNumber: null,
        email: null,
        cellphone: null,
        mailAddress: null,
        addressEE: null,
        addressEEPhone: null,
      });
    }
  };
  //日期限制
  disabledDate = current => {
    const { payDate } = this.state.data;
    return current && current.valueOf() <= moment(payDate).valueOf();
  };
  // 上传附件成功回调
  handleUpload = Oids => {
    this.setState({ uploadOids: Oids });
  };

  // 跳转权限页面
  toClientOther = () => {
    if (this.state.saveData.id !== undefined) {
      this.setState({
        showDetail: true,
        applicationId: this.state.saveData.id,
        flag: this.state.disabled,
      });
    } else {
      Modal.warning({
        title: '提示',
        content: '请先保存！',
      });
    }
  };

  formChange = value => {
    this.setState({ saveFlag: false });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      payeeAccountNumber,
      currencyList,
      data,
      draweeAccountNumber,
      showDetail,
      detailFlag,
      saveFlag,
      btnLoading,
      disabled,
      clientSelectorItem,
    } = this.state;
    const limitDecimals = value => {
      const reg = /^(\-)*(\d+)\.(\d\d).*$/;
      if (typeof value === 'string') {
        return !isNaN(Number(value)) ? value.replace(reg, '$1$2.$3') : '';
      } else if (typeof value === 'number') {
        return !isNaN(value) ? String(value).replace(reg, '$1$2.$3') : '';
      } else {
        return '';
      }
    };

    let newButton = (
      <div className="slide-footer">
        <Button onClick={this.onSubmit} type="primary" loading={btnLoading}>
          提交
        </Button>
        <Button type="primary" htmlType="submit" loading={btnLoading}>
          保存
        </Button>
        <Button onClick={this.onCancel} loading={btnLoading}>
          返回
        </Button>
        <Button
          onClick={this.onDelete}
          loading={btnLoading}
          style={{ color: '#fff', background: '#f04134', 'border-color': '#f04134' }}
        >
          删除
        </Button>
      </div>
    );
    let readOnlyButton = (
      <div className="slide-footer">
        <Button onClick={this.onCancel} loading={btnLoading}>
          返回
        </Button>
      </div>
    );

    return (
      <div className="new-update-client-line">
        <Form onSubmit={this.saveFunction} onChange={this.formChange}>
          <div className="common-item-title">申请信息</div>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="申请编号">
                {getFieldDecorator('applicationCode', {})(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="事务类型">
                {getFieldDecorator('transactionType', {})(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxTransactionTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="事务状态">
                {getFieldDecorator('transactionStatus', {})(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxTransactionStatusOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="申请人">
                {getFieldDecorator('employeeName', {})(<Input disabled={true} />)}
              </FormItem>
            </Col>
          </Row>
          <div className="common-item-title">基本信息</div>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="客户编码">
                {getFieldDecorator('clientNumberList', {
                  rules: [
                    {
                      required:
                        this.props.form.getFieldValue('transactionType') === 'TAX_CLIENT_EDIT',
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  <Chooser
                    selectorItem={clientSelectorItem}
                    labelKey="clientNumber"
                    valueKey="clientNumber"
                    single={true}
                    listExtraParams={{ enabled: true }}
                    disabled={
                      this.props.form.getFieldValue('transactionType') !== 'TAX_CLIENT_EDIT' ||
                      disabled
                    }
                    onChange={this.clientNumberChange}
                    placeholder=""
                  />
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="客户名称">
                {getFieldDecorator('clientName', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="客户类型">
                {getFieldDecorator('clientType', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  <Select
                    disabled={disabled}
                    allowClear={true}
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxClientTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="证件类型">
                {getFieldDecorator('credentialsType', {})(
                  <Select
                    disabled={disabled}
                    allowClear={true}
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxCredentialsTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="证件号码">
                {getFieldDecorator('credentialsNumber', {
                  rules: [
                    {
                      required: this.props.form.getFieldValue('clientType') === '02',
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系人">
                {getFieldDecorator('contacts', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系人电话">
                {getFieldDecorator('contactsPhone', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系地址">
                {getFieldDecorator('contactsAddress', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
          </Row>
          <div className="common-item-title">开票信息</div>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="纳税人名称">
                {getFieldDecorator('taxpayerName', {
                  rules: [
                    {
                      required: this.props.form.getFieldValue('clientType') === '01',
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="纳税人识别号">
                {getFieldDecorator('taxpayerNumber', {
                  rules: [
                    {
                      required: this.props.form.getFieldValue('clientType') === '01',
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="纳税资质">
                {getFieldDecorator('taxQualification', {
                  rules: [
                    {
                      required: this.props.form.getFieldValue('clientType') === '01',
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  <Select
                    disabled={disabled}
                    allowClear={true}
                    placeholder={this.$t('common.please.select')}
                  >
                    {this.state.taxQuaifationOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="地址">
                {getFieldDecorator('address', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="电话">
                {getFieldDecorator('phone', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="银行开户行">
                {getFieldDecorator('openingBank', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="银行账号">
                {getFieldDecorator('accountNumber', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="邮箱">
                {getFieldDecorator('email', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="手机">
                {getFieldDecorator('cellphone', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="收件人">
                {getFieldDecorator('addressEE', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="收件人电话">
                {getFieldDecorator('addressEEPhone', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="邮寄地址">
                {getFieldDecorator('mailAddress', {})(<Input disabled={disabled} />)}
              </FormItem>
            </Col>
          </Row>
          <div className="common-item-title">权限设置</div>
          <Row>
            <Col span={5} offset={1}>
              <a onClick={() => this.toClientOther()}>权限设置</a>
            </Col>
          </Row>
          {disabled ? readOnlyButton : newButton}
        </Form>
        <div
          style={{
            marginTop: 20,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)',
          }}
        >
          <ApproveHistory type="904001" oid={this.props.params.record.documentOid} />
        </div>
        <Modal
          visible={showDetail}
          footer={[
            <Button key="back" onClick={this.onClose}>
              {this.$t({ id: 'common.back' } /*返回*/)}
            </Button>,
          ]}
          width={900}
          closable={false}
          destroyOnClose={true}
          onCancel={this.onClose}
        >
          <div>{this.wrapClose(clientAppOtherDetail)}</div>
        </Modal>
      </div>
    );
  }
}

const wrappedNewUpdateClient = Form.create()(NewUpdateClient);
export default connect()(wrappedNewUpdateClient);
