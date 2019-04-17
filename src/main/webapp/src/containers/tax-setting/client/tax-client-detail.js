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
import WrappedTaxClientOtherDefine from './tax-client-other';
import SlideFrame from 'widget/slide-frame';
class TaxClientDetail extends React.Component {
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
      detailId: undefined,
      clientData: {},
      saveData: {}, //保存后返回新的支付数据
      saveFlag: false, //是否已经保存
      btnLoading: false, //按钮加载
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
      showSlideFrameOther: false,
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

  componentDidMount() {
    this.getInfo();
  }

  //获取客户信息
  getInfo = () => {
    clientService
      .getTaxClientDetail(this.props.match.params.id)
      .then(res => {
        this.setState({ clientData: res.data, getLoading: false });
      })
      .catch(err => {
        console.error(err);
        message.error(err.response.data.message);
        this.setState({ getLoading: false });
      });
  };

  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/basic-tax-information-management/tax-client/tax-client',
      })
    );
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
    let id = this.state.detailId;
    const newProps = {
      params: { id: id, refund: true },
    };
    return React.createElement(content, Object.assign({}, newProps.params, newProps));
  };

  // 上传附件成功回调
  handleUpload = Oids => {
    this.setState({ uploadOids: Oids });
  };

  handleClose = params => {
    this.setState({
      showSlideFrameOther: false,
    });
  };

  // 跳转权限页面
  toClientOther = id => {
    this.setState({
      showSlideFrameOther: true,
    });
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      clientData,
      currencyList,
      data,
      draweeAccountNumber,
      showDetail,
      detailFlag,
      saveFlag,
      btnLoading,
      clientSelectorItem,
      showSlideFrameOther,
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

    return (
      <div className="tax-client-detail">
        <Form>
          <div className="common-item-title">基本信息</div>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="客户编码">
                {getFieldDecorator('clientNumberList', {
                  rules: [
                    {
                      required: false,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                  initialValue: clientData.clientNumber || '',
                })(<Input disabled={true} />)}
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
                  initialValue: clientData.clientName || '',
                })(<Input disabled={true} />)}
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
                  initialValue: clientData.clientType || '',
                })(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxClientTypeOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="证件类型">
                {getFieldDecorator('credentialsType', {
                  initialValue: clientData.credentialsType || '',
                })(
                  <Select disabled placeholder={this.$t('common.please.select')}>
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
                  initialValue: clientData.credentialsNumber || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系人">
                {getFieldDecorator('contacts', {
                  initialValue: clientData.contacts || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系人电话">
                {getFieldDecorator('contactsPhone', {
                  initialValue: clientData.contactsPhone || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="联系地址">
                {getFieldDecorator('contactsAddress', {
                  initialValue: clientData.contactsAddress || '',
                })(<Input disabled={true} />)}
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
                  initialValue: clientData.taxpayerName || '',
                })(<Input disabled={true} />)}
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
                  initialValue: clientData.taxpayerNumber || '',
                })(<Input disabled={true} />)}
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
                  initialValue: clientData.taxQualification || '',
                })(
                  <Select disabled placeholder={this.$t('common.please.select')}>
                    {this.state.taxQuaifationOptions.map(option => {
                      return <Option key={option.value}>{option.label}</Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="地址">
                {getFieldDecorator('address', {
                  initialValue: clientData.address || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="电话">
                {getFieldDecorator('phone', {
                  initialValue: clientData.phone || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="银行开户行">
                {getFieldDecorator('openingBank', {
                  initialValue: clientData.openingBank || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="银行账号">
                {getFieldDecorator('accountNumber', {
                  initialValue: clientData.accountNumber || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="邮箱">
                {getFieldDecorator('email', {
                  initialValue: clientData.email || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={5} offset={1}>
              <FormItem label="手机">
                {getFieldDecorator('cellphone', {
                  initialValue: clientData.cellphone || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="收件人">
                {getFieldDecorator('addressEE', {
                  initialValue: clientData.addressEE || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="收件人电话">
                {getFieldDecorator('addressEEPhone', {
                  initialValue: clientData.addressEEPhone || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
            <Col span={5} offset={1}>
              <FormItem label="邮寄地址">
                {getFieldDecorator('mailAddress', {
                  initialValue: clientData.mailAddress || '',
                })(<Input disabled={true} />)}
              </FormItem>
            </Col>
          </Row>
          <div className="common-item-title">权限设置</div>
          <Row>
            <Col span={5} offset={1}>
              <a onClick={() => this.toClientOther(clientData.id)}>权限设置</a>
            </Col>
          </Row>
          <div className="common-item-title" />
          <div className="slide-footer" style={{ paddingBottom: '10px' }}>
            <Button onClick={this.onCancel} loading={btnLoading}>
              返回
            </Button>
          </div>
        </Form>

        <SlideFrame
          title={'查看权限设置'}
          show={showSlideFrameOther}
          afterClose={this.handleCloseNewSlide}
          onClose={this.handleClose}
        >
          <WrappedTaxClientOtherDefine
            params={{ clientData }}
            onClose={e => {
              this.handleClose(e);
            }}
          />
        </SlideFrame>
      </div>
    );
  }
}

const wrappedTaxClientDetail = Form.create()(TaxClientDetail);
export default connect()(wrappedTaxClientDetail);
