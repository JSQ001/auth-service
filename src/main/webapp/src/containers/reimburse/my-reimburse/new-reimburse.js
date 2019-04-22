import React from 'react';
import { connect } from 'react-redux';
import { Form, Input, Select, Affix, Button, message, Tag, Spin, Modal } from 'antd';
import 'styles/reimburse/new-reimburse.scss';
import SelectReceivables from 'components/Widget/select-receivables';
import ListSelector from 'widget/list-selector';
import reimburseService from 'containers/reimburse/my-reimburse/reimburse.service';
import { routerRedux } from 'dva/router';
import Chooser from 'components/Widget/chooser';
import moment from 'moment';
import SelectContract from 'containers/reimburse/my-reimburse/select-contract-header';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';
import Lov from 'widget/Template/lov';
const FormItem = Form.Item;
const Option = Select.Option;
const CheckableTag = Tag.CheckableTag;
const { TextArea } = Input;

class NewReimburse extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      headerData: {},
      contractShow: false,
      record: {},
      banksInfo: [],
      reTypeDetail: {}, //报账单类型
      linkContract: [], //关联合同
      selectContractParams: {},
      showCompanySelector: false,
      showDepartmentSelector: false,
      showReceiverSelector: false,
      partnerCategoryOptions: [],
      companySelectedData: [],
      deparmentSelectedData: [],
      principals: true,
      currency: [],
      selectedContract: [],
      currentApplyerOid: '',
      viewContractId: '',
      expenseDimensions: [],
      currnetApplyerId: '',
      setOfBooksId: '',
      customFormFields: [],
      formItemLayout: {
        labelCol: {
          span: 6,
        },
        wrapperCol: {
          span: 8,
        },
      },
      formItemLayoutWithOutLabel: {
        wrapperCol: {
          span: 14,
          offset: 6,
        },
      },
      bankData: [{}],
      loading: false,
      isNew: false,
      saveLoading: false,
      defaultUser: {},
      firstRender: true,
    };
  }

  //默认字段 公司 部门 事由
  componentDidMount() {
    this.getReTypeDetail().then(res => {
      if (this.props.match.params.id !== 'new') {
        reimburseService.getReimburseDetailById(this.props.match.params.id).then(res => {
          let record = res.data;
          record.currencyName = record.currencyCode + '-' + record.currencyName;
          this.setState(
            {
              record,
              formSetings: res.data,
              isNew: false,
              customFormFields: res.data.customFormValues,
            },
            () => {
              this.setDimension(
                this.state.expenseDimensions,
                record.companyId,
                record.departmentId,
                record.applicantId
              );
            }
          );
          let values = {
            payeeCategory: {
              key: record.payeeCategory,
              label: record.payeeCategory === 'EMPLOYEE' ? '员工' : '供应商',
            },
          };
          this.props.form.setFieldsValue(values);
          this.getReceivables(record.payeeId, record.payeeCategory);
        });
      } else {
        this.setState({ isNew: true }, () => {
          const { user } = this.props;
          this.setDimension(
            this.state.expenseDimensions,
            user.companyId,
            user.departmentId,
            user.id
          );
        });
        this.listInit();
      }
    });

    //加载币种列表
    this.getCurrencyCodeList();
    this.listUserByTypeId(this.props.match.params.typeId);
  }

  //获取报账单类型信息
  getReTypeDetail = () => {
    return new Promise((resolve, reject) => {
      const { id, typeId } = this.props.match.params;

      let params = {
        expenseReportTypeId: typeId,
        headerId: id !== 'new' ? id : undefined,
      };
      reimburseService.getConfigDetail(params).then(res => {
        let reTypeDetail = res.data;
        this.setState({
          reTypeDetail,
          expenseDimensions: reTypeDetail.expenseDimensions || [],
        });
        let values = {
          payeeCategory: {
            key: reTypeDetail.payeeType === 'VENDER' ? 'VENDER' : 'EMPLOYEE',
            label: reTypeDetail.payeeType === 'VENDER' ? '供应商' : '员工',
          },
        };
        this.props.form.setFieldsValue(values, resolve);
      });
    });
  };

  //加载公司和部门的默认值设置
  listInit = () => {
    const { user, company } = this.props;
    this.setState({
      companySelectedData: [
        {
          companyOid: company.companyOid,
          name: company.name,
          id: company.id,
        },
      ],
      deparmentSelectedData: [
        {
          departmentOid: user.departmentOid,
          departmentName: user.departmentName,
        },
      ],
      applyer: [
        {
          id: user.id,
          userOid: user.userOid,
          fullName: user.fullName,
        },
      ],
      currentApplyerOid: user.userOid,
      currnetApplyerId: user.id,
      setOfBooksId: company.setOfBooksId,
      baseCurrency: company.baseCurrency,
    });
  };

  handleFocus = category => {
    this.refs.chooserBlur.focus(); //取消焦点
    switch (category) {
      case 'company':
        this.setState({ showCompanySelector: true });
        break;
      case 'department':
        this.setState({ showDepartmentSelector: true });
        break;
      // case 'costCenter':
      //   this.setState({showCostCenterSelector:true});
      case 'receiver':
        this.setState({ showReceiverSelector: true });
        break;
    }
  };

  getCurrency = () => {
    let params = {
      enable: true,
      setOfBooksId: this.props.company.setOfBooksId,
      tenantId: this.props.company.tenantId,
    };
    reimburseService.getCurrency(params).then(res => {
      this.setState({
        currency: res.data.records,
      });
    });
  };

  getCurrencyCodeList = () => {
    reimburseService
      .getCurrencyCode()
      .then(res => {
        this.setState({
          currencyCodeList: res.data,
        });
      })
      .catch(err => {
        message.error(`网络错误，请稍后再重试！`);
      });
  };

  //获得自定义列表
  getCustomList = customEnumerationOid => {
    reimburseService
      .getCustomEnumeration(customEnumerationOid)
      .then(res => {})
      .catch(err => {
        message.error('网络错误！请稍后重试');
      });
  };
  //收款方回调
  handlePayeeId = value => {
    let type = this.props.form.getFieldValue('payeeCategory');
    this.props.form.setFieldsValue({ accountNumber: {}, accountName: '' });
    this.getReceivables(value.key, type && type.key);
  };

  //获取收款方
  getReceivables = (value, payeeCategory) => {
    if (!value) return;
    let flag = payeeCategory === 'EMPLOYEE';
    let method = flag ? 'getAccountByUserId' : 'getAccountByVendorId';
    let defaultBank = false;
    reimburseService[method](value).then(res => {
      defaultBank = res.data.find(item => item[flag ? 'primary' : 'primaryFlag']);
      res.data.length === 0 &&
        message.warning('该收款方没有银行信息，请先维护改收款方下银行信息！');
      if (!!defaultBank) {
        this.props.form.setFieldsValue({
          accountNumber: {
            key: defaultBank.bankAccount || defaultBank.bankAccountNo,
            label: defaultBank.bankCode + '-' + defaultBank.bankName,
          },
          accountName: defaultBank.bankAccountName,
        });
      }
      this.setState({ banksInfo: res.data });
    });
  };

  //获取收款方银行账户
  accountNumberChange = value => {
    let bankInfo = this.state.banksInfo.find(
      item => (item.bankAccount || item.bankAccountNo) === value.key
    );
    this.props.form.setFieldsValue({ accountName: bankInfo.bankAccountName });
  };

  //收款方类型
  getContractType = () => {
    this.getSystemValueList(2107).then(res => {
      let partnerCategoryOptions = res.data.values || [];
      this.setState({ partnerCategoryOptions });
    });
  };

  handleBankChange = value => {
    this.props.form.resetFields(['bankName']);
    let bankName = '';
    for (let i of this.state.bankData) {
      if (value === i.number) {
        bankName = i.bankName;
        break;
      }
    }
    this.props.form.setFieldsValue({
      bankName: bankName,
    });
  };

  //新建保存
  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFields((err, value) => {
      if (err) return;
      this.setState({ saveLoading: true });
      const { record, selectedContract, expenseDimensions, currency } = this.state;
      value.companyId = value.companyId[0].id;
      value.departmentId = value.departmentId[0].departmentId;
      value.applicantId = value.applicantId.id;
      value.accountNumber && (value.accountNumber = value.accountNumber.key);
      value.documentTypeId = this.props.match.params.typeId;
      value.payeeCategory = value.payeeCategory && value.payeeCategory.key;
      value.payeeId = value.payeeId && value.payeeId.key;
      value.expenseDimensions = expenseDimensions.map(item => {
        item.headerFlag && (item.value = value[item.dimensionField].key);
        return item;
      });
      value.contractHeaderId = selectedContract.length
        ? selectedContract[0].contractHeaderId
        : record.contractHeaderId;
      if (record.id) {
        value.id = record.id;
        value.currencyCode = record.currencyName && (value.currencyCode = record.currencyCode);
        value.status = record.status;

        for (let name in record) {
          if (!value[name]) {
            value[name] = record[name];
          }
        }
      }
      value.exchangeRate =
        (currency.find(item => value.currencyCode === item.currencyCode) || {}).rate ||
        record.exchangeRate;
      reimburseService
        .newReimburse(value)
        .then(res => {
          if (200 === res.status) {
            this.setState({ saveLoading: false });
            message.success('操作成功');
            this.props.dispatch(
              routerRedux.push({
                pathname: `/my-reimburse/my-reimburse/reimburse-detail/${res.data.id}`,
              })
            );
          } else {
            message.error('操作失败');
            this.setState({ saveLoading: false });
          }
        })
        .catch(err => {
          message.error('操作失败：' + err.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };
  //返回按钮
  onCancel = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/my-reimburse`,
      })
    );
  };

  onBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/my-reimburse/my-reimburse/reimburse-detail/${this.props.match.params.id}`,
      })
    );
  };

  //选择收款方类型
  handlePartnerCategory = record => {
    this.props.form.setFieldsValue({
      accountName: '',
      payeeId: {},
      accountNumber: { key: '', label: '' },
    });
  };
  //选择合同方
  clickContractSelect = open => {
    const { record } = this.state;
    if (!open) return;
    let values = this.props.form.getFieldsValue();
    let message = '';
    !values.companyId.length && (message += `公司`);
    !values.currencyCode && (message += `${!!message ? '、' : ''}币种`);
    //按照 币种和公司来关联
    /*if (values.payeeId) {
      !values.payeeId.key && (message += `${!!message ? '、' : ''}收款方 `);
    }*/

    if (!!message) {
      message.warning(`${this.$t('common.please.enter')}${message}`);
      return;
    }
    let selectContractParams = {
      companyId: values.companyId[0].id,
      partnerCategory: values.partnerCategory,
      //按照 币种和公司来关联
      //partnerId: values.payeeId ? values.payeeId.key : undefined,
      documentType: 'PREPAYMENT_REQUISITION',
      currency: values.currencyCode,
    };
    if (record.id && record.currencyName === selectContractParams.currency) {
      selectContractParams.currency = record.currencyCode;
    }
    this.setState({
      showListSelector: true,
      selectContractParams,
    });
  };

  //取消选择合同
  handleContractCancel = () => {
    this.setState({
      showListSelector: false,
    });
  };

  //选择合同
  handleContractOk = result => {
    if (result.result.length) {
      let value = [];
      this.setState(
        {
          showListSelector: false,
          selectedContract: result.result,
        },
        () => {
          this.props.form.setFieldsValue({
            contractHeaderId: {
              key: result.result[0].contractId,
              label: result.result[0].contractNumber,
            },
          });
        }
      );
    }
  };

  userInit = user => {
    reimburseService
      .getUserInfoByTypeId(user.userOid)
      .then(res => {
        let temp = res.data;
        let company = [{ id: temp.companyId, name: temp.companyName }];
        let department = [{ departmentId: temp.departmentId, name: temp.departmentName }];
        this.props.form.setFieldsValue({
          companyId: company,
          departmentId: department,
        });
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  userChange = user => {
    reimburseService
      .getUserInfoByTypeId(user.userOid)
      .then(res => {
        let temp = res.data;
        let company = [{ id: temp.companyId, name: temp.companyName }];
        let department = [{ departmentId: temp.departmentId, name: temp.departmentName }];
        this.props.form.setFieldsValue(
          {
            companyId: company,
            departmentId: department,
          },
          () => {
            this.userOrCompanyOrUnitChange('oldId', 'oldId', user.id);
          }
        );
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  unitChange = value => {
    this.userOrCompanyOrUnitChange('oldId', value[0].departmentId, 'oldId');
  };

  companyChange = value => {
    this.userOrCompanyOrUnitChange(value[0].id, 'oldId', 'oldId');
  };

  userOrCompanyOrUnitChange = (companyId, unitId, userId) => {
    let oldCompany = this.props.form.getFieldValue('companyId');
    let oldUnit = this.props.form.getFieldValue('departmentId');
    let oldUser = this.props.form.getFieldValue('applicantId');
    if (companyId === 'oldId') {
      companyId = oldCompany[0].id;
    }
    if (unitId === 'oldId') {
      unitId = oldUnit[0].departmentId;
    }
    if (userId === 'oldId') {
      userId = oldUser.id;
    }
    this.setDimension(this.state.expenseDimensions, companyId, unitId, userId);
  };

  setDimension = (dimensions, companyId, unitId, userId) => {
    if (dimensions !== null && dimensions.length !== 0) {
      let dimensionIds = dimensions.map(item => item.dimensionId);
      reimburseService
        .getDimensionItemsByIds(dimensionIds, companyId, unitId, userId)
        .then(res => {
          let temp = res.data;
          dimensions.forEach(e => {
            let items = temp.find(o => o.id === e.dimensionId)['subDimensionItemCOS'];
            e.options = items;
          });
          this.setState(
            {
              expenseDimensions: dimensions || [],
            },
            () => {
              if (this.state.firstRender) {
                dimensions.forEach(e => {
                  let dimensionItem = e.options.find(o => o.id === e.value);
                  if (dimensionItem) {
                    this.props.form.setFieldsValue({
                      [`${e.dimensionField}`]: { key: e.value, label: e.valueName },
                    });
                  }
                });
                this.setState({ firstRender: false });
              }
            }
          );
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  listUserByTypeId = typeId => {
    reimburseService
      .listUserByTypeId(typeId)
      .then(res => {
        if (res.data) {
          let { defaultUser } = this.state;
          const currentUser = res.data.find(o => o.id === this.props.user.id);
          if (currentUser) {
            defaultUser = currentUser;
          } else {
            defaultUser = res.data[0];
          }
          this.setState({ defaultUser });
          this.userInit(defaultUser);
        }
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  //渲染维度
  renderDim = () => {
    const { getFieldDecorator } = this.props.form;
    const { formItemLayout, expenseDimensions } = this.state;

    let content = expenseDimensions.map(item => {
      if (item.headerFlag) {
        return (
          <FormItem {...formItemLayout} label={item.name}>
            {getFieldDecorator(item.dimensionField, {
              rules: [{ required: item.requiredFlag, message: `请选择${item.name}` }],
              // initialValue: {key: item.value, label: item.valueName},
            })(
              <Select labelInValue>
                {(item.options || []).map(i => (
                  <Select.Option key={i.id}>{i.dimensionItemName}</Select.Option>
                ))}
              </Select>
            )}
          </FormItem>
        );
      }
    });
    return content;
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const { user } = this.props;
    const {
      showCompanySelector,
      companySelectedData,
      showDepartmentSelector,
      selectContractParams,
      showListSelector,
      currency,
      formItemLayout,
      record,
      linkContract,
      expenseDimensions,
      partnerCategoryOptions,
      isNew,
      banksInfo,
      contractShow,
      reTypeDetail,
      viewContractId,
      selectedContract,
      defaultUser,
    } = this.state;
    return (
      <div className="new-contract " style={{ marginBottom: '70px', paddingBottom: '60px' }}>
        <Spin spinning={false}>
          <Form onSubmit={this.handleSubmit}>
            <FormItem {...formItemLayout} label="申请人">
              {getFieldDecorator('applicantId', {
                rules: [{ required: true, message: '请选择申请人' }],
                initialValue: isNew
                  ? { id: defaultUser.id, fullName: defaultUser.fullName }
                  : { id: record.applicantId, fullName: record.applicantName },
              })(
                <Lov
                  code="report_user_authorize"
                  valueKey="id"
                  labelKey="fullName"
                  onChange={this.userChange}
                  allowClear={false}
                  single
                  extraParams={{ expenseReportTypeId: this.props.match.params.typeId }}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="公司">
              {getFieldDecorator('companyId', {
                rules: [{ required: true, message: '请选择公司' }],
                initialValue: [
                  {
                    id: record.companyId || user.companyId,
                    name: record.companyName || user.companyName,
                  },
                ],
              })(
                <Chooser
                  labelKey="name"
                  valueKey="id"
                  single={true}
                  type="select_company_reimburse"
                  onChange={this.companyChange}
                  listExtraParams={{
                    setOfBooksId: this.props.company.setOfBooksId,
                    tenantId: this.props.company.tenantId,
                  }}
                />
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="部门">
              {getFieldDecorator('departmentId', {
                rules: [{ required: true, message: '请选择部门' }],
                initialValue: [
                  {
                    departmentId: record.departmentId || user.departmentId,
                    name: record.departmentName || user.departmentName,
                  },
                ],
              })(
                <Chooser
                  labelKey="name"
                  valueKey="departmentId"
                  single={true}
                  onChange={this.unitChange}
                  type="select_department_reimburse"
                />
              )}
            </FormItem>
            {reTypeDetail.id &&
              !reTypeDetail.multiPayee && (
                <div>
                  <FormItem {...formItemLayout} label="收款方类型">
                    {getFieldDecorator('payeeCategory', {
                      rules: [
                        {
                          required: true,
                          message: this.$t({ id: 'common.please.enter' }),
                        },
                        {
                          validator: (item, value, callback) => {
                            if (!value.key) {
                              callback(this.$t({ id: 'common.please.select' }));
                            }
                            callback();
                          },
                        },
                      ],
                      initialValue: {
                        key: record.payeeCategory || 'EMPLOYEE',
                        label: record.partnerCategoryName || '员工',
                      },
                    })(
                      <Select
                        labelInValue
                        disabled={reTypeDetail.payeeType !== 'BOTH'}
                        onChange={this.handlePartnerCategory}
                        onDropdownVisibleChange={
                          partnerCategoryOptions.length === 0 ? this.getContractType : () => {}
                        }
                        placeholder={this.$t({ id: 'common.please.select' })}
                      >
                        {partnerCategoryOptions.map(item => (
                          <Option key={item.value}>{item.name}</Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                  <FormItem {...formItemLayout} label={'收款方'}>
                    {getFieldDecorator('payeeId', {
                      initialValue: {
                        key: record.payeeId || '',
                        label: record.payName || '',
                      },
                      rules: [
                        {
                          required: true,
                          message: this.$t({ id: 'common.please.enter' }),
                        },
                        {
                          validator: (item, value, callback) => {
                            if (!value.key) {
                              callback(this.$t({ id: 'common.please.select' }));
                            }
                            callback();
                          },
                        },
                      ],
                    })(
                      <SelectReceivables
                        onChange={this.handlePayeeId}
                        companyId={this.props.form.getFieldValue('companyId')[0].id}
                        type={
                          this.props.form.getFieldValue('payeeCategory')
                            ? this.props.form.getFieldValue('payeeCategory').key
                            : ''
                        }
                      />
                    )}
                  </FormItem>
                  <FormItem {...formItemLayout} label="收款方银行账户">
                    {getFieldDecorator('accountNumber', {
                      initialValue: {
                        key: record.accountNumber || '',
                        label: record.id ? record.accountNumber + '-' + record.accountName : '',
                      },
                      rules: [
                        {
                          required: true,
                          message: '请选择',
                        },
                      ],
                    })(
                      <Select labelInValue onChange={this.accountNumberChange}>
                        {banksInfo.map(item => (
                          <Option key={item.bankAccount || item.bankAccountNo}>
                            {item.bankAccount || item.bankAccountNo}
                          </Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                  <FormItem {...formItemLayout} label="收款方户名">
                    {getFieldDecorator('accountName', {
                      initialValue: record.bankAccountName,
                      rules: [
                        {
                          required: false,
                          message: '请输入',
                        },
                      ],
                    })(<Input disabled />)}
                  </FormItem>
                </div>
              )}
            {//展示维度
            !!expenseDimensions.length && this.renderDim()}
            <FormItem {...formItemLayout} label="币种">
              {getFieldDecorator('currencyCode', {
                rules: [{ required: true, message: '请选择币种' }],
                initialValue: record.currencyName,
              })(
                <Select onFocus={this.getCurrency} placeholder="请选择">
                  {currency.map(value => (
                    <Option key={value.currencyCode}>
                      {value.currencyCode + '-' + value.currencyName}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
            <FormItem {...formItemLayout} label="备注">
              {getFieldDecorator('description', {
                rules: [{ required: false, message: '请选择备注' }],
                initialValue: record.description,
              })(<TextArea />)}
            </FormItem>
            {reTypeDetail.associateContract && (
              <FormItem {...formItemLayout} label="关联合同">
                {getFieldDecorator('contractHeaderId', {
                  rules: [{ required: reTypeDetail.contractRequired, message: '请选择合同' }],
                  initialValue: {
                    key: record.contractHeaderId | '',
                    label: record.contractNumber || '',
                  },
                })(
                  <Select
                    allowClear
                    labelInValue
                    onChange={e => {
                      this.setState({ contractValue: [] });
                    }}
                    dropdownStyle={{ display: 'none' }}
                    onDropdownVisibleChange={this.clickContractSelect}
                  />
                )}
                <div style={{ marginTop: '4px' }}>
                  {linkContract.length == 0
                    ? '注：根据币种选择合同'
                    : `序号：${lineNumber} | 付款计划日期：${moment(dueDate).format('YYYY-MM-DD')}`}
                </div>
              </FormItem>
            )}
            <input ref="chooserBlur" style={{ position: 'absolute', top: '-100vh', zIndex: -1 }} />
            <Affix
              offsetBottom={0}
              style={{
                position: 'fixed',
                bottom: 0,
                marginLeft: '-35px',
                width: '100%',
                height: '50px',
                boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                background: '#fff',
                lineHeight: '50px',
              }}
            >
              <Button
                style={{ marginLeft: 20, marginRight: 20 }}
                disabled={this.state.loading}
                loading={this.state.saveLoading}
                type="primary"
                onClick={this.handleSubmit}
              >
                {isNew ? '下一步' : '确定'}
              </Button>
              {isNew ? (
                <Button onClick={this.onCancel}>取消</Button>
              ) : (
                <Button onClick={this.onBack}>返回</Button>
              )}
            </Affix>
          </Form>
        </Spin>
        <SelectContract
          visible={showListSelector}
          onCancel={this.handleContractCancel}
          onOk={this.handleContractOk}
          single={true}
          viewContract={value => {
            this.setState({
              contractShow: true,
              viewContractId: value,
            });
          }}
          params={selectContractParams}
          selectedData={selectedContract || []}
        />
        <Modal
          title="合同详情"
          visible={contractShow}
          onCancel={() => {
            this.setState({ contractShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          destroyOnClose={true}
          footer={null}
        >
          <ContractDetail id={viewContractId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

NewReimburse = Form.create()(NewReimburse);

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
)(NewReimburse);
