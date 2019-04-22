/*eslint-disable*/
import React, { Component } from 'react';
import { connect } from 'dva';
import { Form, Input, Row, Col, Button, Select, message, InputNumber } from 'antd';
import Chooser from 'widget/chooser';
import moment from 'moment';
import config from 'config';
import Upload from 'widget/upload';
import { routerRedux } from 'dva/router';
import httpFetch from 'share/httpFetch';
import service from './service';

const bottomStyle = {
  position: 'fixed',
  bottom: 0,
  marginLeft: '-35px',
  width: '100%',
  height: '50px',
  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
  background: '#fff',
  lineHeight: '50px',
};

class expInputTaxNew extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isNew: true,
      currencyList: [],
      transferTypeList: [],
      newDefaultData: {
        transferTypeValue: '',
        transferTypeName: '',
        useTypeValue: '',
        useTypeName: '',
      },
      useTypeList: [],
      //fileList: [], // 已存在的附件信息
      uploadOIDs: [], // 用于新增时的附件信息
      loading: false,
      rate: 1,
      saveLoading: false,
    };
  }

  componentDidMount() {
    if (this.props.match.params.id) {
      service.getBusinessReceiptHeadValue(this.props.match.params.id).then(res => {
        let fileList = res.data.attachments
          ? res.data.attachments.map(o => ({
              ...o,
              uid: o.attachmentOid,
              name: o.fileName,
              status: 'done',
            }))
          : [];
        this.setState({
          uploadOIDs: fileList.map(o => o.uid),
          fileList,
          headerData: res.data,
        });
      });
    }
    // 获取币种列表
    const { company } = this.props;
    service.getCurrencyList(company.companyOid).then(res => {
      this.setState({ currencyList: res.data });
    });

    // 业务大类
    this.getSystemValueList('transferType')
      .then(res => {
        const transferTypeList = res.data.values;
        const { newDefaultData } = this.state;
        newDefaultData.transferType = transferTypeList[0].value;
        newDefaultData.transferTypeName = transferTypeList[0].name;
        this.setState({ transferTypeList, newDefaultData });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    // 用途类型
    this.getSystemValueList('useType')
      .then(res => {
        const useTypeList = res.data.values;
        const { newDefaultData } = this.state;
        newDefaultData.useTypeValue = useTypeList[0].value;
        newDefaultData.useTypeName = useTypeList[0].name;
        this.setState({ useTypeList, newDefaultData });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  // 获取汇率
  getExchangeRate = values => {
    // console.log(values);
    const currencyDate = moment(new Date()).format('YYYY-MM-DD');
    const url = `${config.mdataUrl}/api/currency/rate/company/standard/currency/get?currency=${
      values.key
    }&currencyDate=${currencyDate}`;
    httpFetch
      .get(url)
      .then(res => {
        this.setState({ rate: res.data.rate });
      })
      .catch(err => {
        message.error(message('pay.get.tax.failed'), err.response.data.message);
      });
  };

  // 表单提交
  handleSave = e => {
    e.preventDefault();
    let params = {};
    const { form } = this.props;
    form.validateFields((err, values) => {
      if (err) return;
      const { rate, uploadOIDs, headerData } = this.state;
      this.setState({ loading: true });
      params = {
        id: headerData ? headerData.id : '',
        tenantId: this.props.user.tenantId,
        setOfBooksId: this.props.company.setOfBooksId,
        employeeId: this.props.user.id,
        companyId: this.props.company.id,
        departmentId: values.department[0].departmentId,
        transferDate: moment().format(),
        transferType: values.transferType.key,
        transferProportion: values.transferProportion / 100 || 1,
        useType: values.useType.key,
        currencyCode: values.currency ? values.currency.key : '',
        rate,
        description: values.description,
        attachmentOid: uploadOIDs && uploadOIDs.join(),
      };
      console.log(params);
      service
        .headerInsertOrUpdate(params)
        .then(res => {
          message.success(this.$t('common.operate.success'));
          this.setState({ loading: false }, () => {
            this.handleNextPage(res.data.id, res.data.transferType, res.data.currencyCode);
          });
        })
        .catch(err => {
          console.log(err);
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  // 上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };

  onBack = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/exp-input-tax/exp-input-tax/exp-input-tax',
      })
    );
  };

  // 跳转至下一页面
  handleNextPage = (id, transferType, currencyCode) => {
    const { headerData } = this.state;
    if (headerData && headerData.id) {
      this.handleCancel();
    } else {
      const { dispatch } = this.props;
      dispatch(
        routerRedux.push({
          pathname: `/exp-input-tax/exp-input-tax/input-tax-business-receipt/${id}/${transferType}`,
        })
      );
    }
  };

  handleCancel = () => {
    const { dispatch } = this.props;
    const { headerData } = this.state;
    dispatch(
      routerRedux.push({
        pathname: `/exp-input-tax/exp-input-tax/input-tax-business-receipt/${headerData.id}/${
          headerData.transferType
        }`,
      })
    );
  };
  validator = (rule, value, callback) => {
    if (value <= 0 || value > 100) {
      callback('请输入1～100之间的数字！');
      return;
    }
    callback();
  };

  render() {
    const FormItem = Form.Item;
    const { form, user, company } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const { headerData: params } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 12 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
    const colWidth = 10;
    const {
      currencyList,
      transferTypeList,
      newDefaultData,
      useTypeList,
      fileList,
      loading,
      saveLoading,
    } = this.state;
    let { isNew } = this.state;
    isNew = params && params.id ? false : true;

    return (
      <div
        className="new-contract"
        style={!isNew ? { marginTop: 10, marginBottom: 0 } : { marginBottom: 60, marginTop: 10 }}
      >
        <Form onSubmit={this.handleSave}>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              {/* 申请人 */}
              <FormItem {...formItemLayout} label={this.$t('common.applicant')}>
                {getFieldDecorator('employeeId', {
                  rules: [{ required: true }],
                  initialValue: params && params.id ? params.fullName : user.userName,
                })(<Input disabled />)}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem label={this.$t('acp.company')} {...formItemLayout}>
                {getFieldDecorator('company', {
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                  initialValue:
                    params && params.id
                      ? [{ id: params.companyId, name: params.companyName }]
                      : [{ id: company.id, name: company.name }],
                })(
                  <Chooser
                    type="company"
                    labelKey="name"
                    valueKey="id"
                    disabled={!isNew}
                    single
                    listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    showClear={false}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem {...formItemLayout} label={this.$t('common.department')}>
                {getFieldDecorator('department', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue:
                    params && params.id
                      ? [
                          {
                            path: params.departmentName,
                            departmentId: params.departmentId,
                          },
                        ]
                      : [
                          {
                            path: user.departmentPath,
                            departmentId: user.departmentId,
                          },
                        ],
                })(
                  <Chooser
                    type="department_document"
                    labelKey="path"
                    valueKey="departmentId"
                    single
                    listExtraParams={{ tenantId: user.tenantId }}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem {...formItemLayout} label={this.$t('acp.currency')}>
                {getFieldDecorator('currency', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue:
                    params && params.id
                      ? {
                          key: params.currencyCode,
                          label: `${params.currencyCode}-${params.currencyName},`,
                        }
                      : {
                          key: company.baseCurrency,
                          label: `${company.baseCurrency}-${company.baseCurrencyName}`,
                        },
                })(
                  <Select
                    placeholder={this.$t('common.please.select')}
                    onChange={this.getExchangeRate}
                    labelInValue
                  >
                    {currencyList.map(item => {
                      return (
                        <Select.Option key={item.currency}>
                          {item.currency}-{item.currencyName}
                        </Select.Option>
                      );
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem {...formItemLayout} label={this.$t('tax.business.categories')}>
                {getFieldDecorator('transferType', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue:
                    params && params.id
                      ? {
                          key: params.transferType,
                          label: params.transferTypeName,
                        }
                      : {
                          key: newDefaultData.transferType,
                          label: newDefaultData.transferTypeName,
                        },
                })(
                  <Select placeholder={this.$t('common.please.select')} labelInValue>
                    {transferTypeList.map(item => {
                      return <Select.Option key={item.value}>{item.name}</Select.Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              {getFieldValue('transferType').key === 'ALL_TRANSFER' ? (
                ''
              ) : (
                <FormItem
                  {...formItemLayout}
                  label={
                    getFieldValue('transferType').key === 'PART_TRANSFER'
                      ? this.$t('tax.turn.out.proportion')
                      : this.$t('expense.invoice.tax.rate')
                  }
                  colon
                >
                  {getFieldDecorator('transferProportion', {
                    initialValue: params && params.id ? params.transferProportion : '',
                    rules: [
                      {
                        validator: this.validator,
                      },
                    ],
                  })(
                    <InputNumber
                      min={0.001}
                      max={100}
                      formatter={value => `${value}%`}
                      parser={value => value.replace('%', '')}
                    />
                  )}
                </FormItem>
              )}
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem {...formItemLayout} label={this.$t('tax.use.type')}>
                {getFieldDecorator('useType', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue:
                    params && params.id
                      ? {
                          key: params.useType,
                          label: params.useTypeName,
                        }
                      : {
                          key: newDefaultData.useTypeValue,
                          label: newDefaultData.useTypeName,
                        },
                })(
                  <Select placeholder={this.$t('common.please.select')} labelInValue>
                    {useTypeList.map(item => {
                      return <Select.Option key={item.value}>{item.name}</Select.Option>;
                    })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colWidth}>
              <FormItem label={this.$t('common.remark')} {...formItemLayout}>
                {getFieldDecorator('description', {
                  initialValue: params && params.id ? params.description : '',
                })(
                  <Input.TextArea
                    autosize={{ minRows: 3 }}
                    placeholder={this.$t('expense.please.enter.remark.not.required')}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout} style={{ marginBottom: 40 }}>
            <Col span={colWidth}>
              <FormItem label={this.$t('acp.fileInfo')} {...formItemLayout}>
                {getFieldDecorator('attachmentOid')(
                  <Upload
                    attachmentType="BUDGET_JOURNAL"
                    uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                    fileNum={9}
                    uploadHandle={this.handleUpload}
                    defaultFileList={fileList}
                    defaultOids={params ? params.attachmentOidList : []}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          {isNew ? (
            <div style={bottomStyle}>
              <Button
                type="primary"
                style={{ margin: '0 20px' }}
                htmlType="submit"
                loading={loading}
              >
                {/* 下一步 */}
                {this.$t('acp.next')}
              </Button>
              <Button onClick={this.onBack} style={{ marginRight: '10px' }}>
                {/* 返回 */}
                {this.$t('common.return')}
              </Button>
            </div>
          ) : (
            <div style={bottomStyle}>
              <Button
                className="btn"
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{ margin: '0 20px' }}
              >
                {this.$t('common.save')}
              </Button>
              <Button className="btn" onClick={this.handleCancel}>
                {this.$t('common.cancel')}
              </Button>
            </div>
          )}
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

export default connect(map)(Form.create()(expInputTaxNew));
