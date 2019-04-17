/*eslint-disable*/
import React, { Component } from 'react';
import { connect } from 'dva';
import { Form, Input, Row, Col, Button, Select, message, Affix } from 'antd';
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
  width: '100%',
  height: '50px',
  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
  background: '#fff',
  lineHeight: '50px',
  paddingLeft: '20px',
  zIndex: 1,
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
    if (!this.state.isNew && this.props.params.id) {
      let fileList = this.props.params.attachments
        ? this.props.params.attachments.map(o => ({
            ...o,
            uid: o.attachmentOid,
            name: o.fileName,
            status: 'done',
          }))
        : [];

      this.setState({
        uploadOIDs: fileList.map(o => o.uid),
        fileList,
      });
      console.log(this);
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
      this.setState({ loading: true });
      const def = this.props;
      const { rate, uploadOIDs } = this.state;
      params = {
        id: def.params.id,
        tenantId: def.user.tenantId,
        setOfBooksId: def.company.setOfBooksId,
        employeeId: def.user.id,
        companyId: def.company.id,
        departmentId: values.department[0].departmentId,
        transferDate: moment().format(),
        transferType: values.transferType.key,
        transferProportion: values.transferProportion || 1,
        useType: values.useType.key,
        currencyCode: values.currency ? values.currency.key : '',
        rate,
        description: values.description,
        attachmentOid: uploadOIDs && uploadOIDs.join(),
      };
    });
    service
      .headerInsertOrUpdate(params)
      .then(res => {
        message.success(this.$t('common.operate.success'));
        this.setState({ loading: false }, () => {
          this.handleNextPage(res.data.id, params.transferType, params.currencyCode);
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
        this.setState({ loading: false });
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
    this.handleCancel();
    this.props.getHeaderList();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/exp-input-tax/exp-input-tax/input-tax-business-receipt/${id}/${transferType}`,
      })
    );
  };

  handleCancel = () => {
    const { onClose } = this.props;
    if (onClose) {
      onClose();
    }
  };

  render() {
    const FormItem = Form.Item;
    const { form, user, company, params } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const formItemLayout =
      params && params.id
        ? {
            labelCol: { span: 8 },
            wrapperCol: { span: 10 },
          }
        : {
            labelCol: {
              xs: { span: 12 },
              sm: { span: 6 },
            },
            wrapperCol: {
              xs: { span: 24 },
              sm: { span: 16 },
            },
          };
    const colWidth = params ? 24 : 10;
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
                        required: true,
                        message: this.$t('common.please.enter'),
                      },
                      {
                        type: 'number',
                        transform: value => parseInt(value, 10),
                        // message: this.$t('org.role.type-number'), // "必须是数字"
                        pattern: /^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/gi,
                        message: this.$t('tax.number.greater.than.zero'), // '且为大于零的数字'
                      },
                    ],
                  })(<Input placeholder={this.$t('common.please.select')} />)}
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
            <Affix style={bottomStyle}>
              <Button onClick={this.onBack} style={{ marginRight: '10px' }}>
                {/* 返回 */}
                {this.$t('common.return')}
              </Button>
              <Button type="primary" htmlType="submit" loading={loading}>
                {/* 下一步 */}
                {this.$t('acp.next')}
              </Button>
            </Affix>
          ) : (
            <div className="slide-footer">
              <Button
                className="btn"
                type="primary"
                htmlType="submit"
                loading={saveLoading}
                onClick={this.handleEdit}
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
