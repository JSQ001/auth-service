import React, { Component } from 'react';
import { Form, Input, Button, Select, message, DatePicker } from 'antd';
import { connect } from 'dva';
import moment from 'moment';
import Chooser from 'widget/chooser';
import Upload from 'widget/upload-button';
import 'styles/fund/account.scss';
import config from 'config';
import accountService from './account.service';

const { Option } = Select;
const FormItem = Form.Item;

class OpenAccountMaintain extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载中
      isNew: true, // 是否为新建
      model: {}, // 保存数据
      currencyList: [], // 币种
      uploadOIDs: [], // 附件oid
      bankOptions: [], // 开户银行列表
      banknumber: {
        key: '',
        label: '',
      }, // 联行号
      openProvince: '', // 开户省
      openCity: '', // 开户市
      bankFetching: false,
      approveStatus: '', // 单据状态
      isTrue: false, // 单据状态为已审批和审批中的不可修改信息
    };
  }

  componentDidMount() {
    const { params } = this.props;
    if (params.id) {
      this.getAccountHead(params.id);
    }
    // console.log(params.approveStatus)
    if (params.approveStatus === 'ZJ_APPROVED' || params.approveStatus === 'ZJ_PENGDING') {
      this.setState({
        isTrue: true,
      });
    }
    this.getCurrencyList();
    this.getAccountBank();
  }

  /**
   * 根据ID获取账户头
   */
  getAccountHead = id => {
    accountService
      .getAccountHead(id)
      .then(res => {
        const { attachmentOidList } = res.data;
        const fileList = res.data.attachments
          ? res.data.attachments.map(item => ({
              ...item,
              uid: item.attachmentOid,
              name: item.fileName,
              status: 'done',
            }))
          : [];
        this.setState({
          isNew: false,
          model: res.data,
          approveStatus: res.data.approveStatus,
          uploadOIDs: attachmentOidList,
          fileList,
        });
      })
      .catch(err => {
        message.error(err.message);
      });
  };

  /**
   * 点击选择银行
   */
  chooseBank = value => {
    /* eslint-disable */
    this.props.form.setFieldsValue({
      banknumber: {
        key: value[0].bankCode,
        label: value[0].bankCode,
      },
      /*
      openProvince: {
        key: value[0].provinceCode,
        label: value[0].province ? value[0].province : value[0].city,
      },
      openCity: {
        key: value[0].cityCode,
        label: value[0].city ? value[0].city : value[0].province,
      },
      */
    });
    /* eslint-disable */
  };

  /**
   * 获取币种
   */
  getCurrencyList = () => {
    const { company } = this.props;
    accountService
      .getCurrencyList(company.companyOid)
      .then(res => {
        this.setState({ currencyList: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  /**
   * 获取开户银行列表
   */
  getAccountBank = () => {
    accountService
      .getAccountBank()
      .then(res => {
        if (res.data.length > 0) {
          this.setState({
            bankOptions: res.data,
            bankFetching: true,
          });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };
  /**
   * 取消
   */
  handleCancel = value => {
    this.props.onClose(value);
  };

  /**
   * 上传附件
   */
  handleUpload = OIDs => {
    const { params } = this.props;
    const { isTrue } = this.state;
    if (isTrue) {
      if (params.approveStatus === 'ZJ_APPROVED') {
        message.error('单据状态为已审批，信息不可修改');
      } else if (params.approveStatus === 'ZJ_PENGDING') {
        message.error('单据状态为审批中，信息不可修改');
      }
    } else {
      let { uploadOIDs } = this.state;
      console.log(uploadOIDs);
      OIDs.forEach(item => {
        uploadOIDs.push(item);
      });
      console.log(uploadOIDs);
      this.setState({
        uploadOIDs: uploadOIDs,
      });
    }
  };

  /**
   * 表单提交
   */
  handleSave = e => {
    const { form, user, params } = this.props;
    const { isTrue } = this.state;
    e.preventDefault();
    if (isTrue) {
      if (params.approveStatus === 'ZJ_APPROVED') {
        message.error('单据状态为已审批，信息不可修改');
      } else if (params.approveStatus === 'ZJ_PENGDING') {
        message.error('单据状态为审批中，信息不可修改');
      }
    } else {
      form.validateFields((err, values) => {
        let saveData = {};
        if (err) return;
        this.setState({ loading: true });
        const { uploadOIDs, isNew, model } = this.state;
        if (isNew) {
          saveData = {
            requisitionDate: moment(values.requisitionDate).format(),
            employeeId: user.id,
            currencyCode: values.currencyCode,
            remarks: values.remarks,
            companyId: values.company[0].id,
            departmentId: values.company[0].departmentId || '1084803809249693697',
            openProvince: values.bankBranch[0].provinceCode,
            openCity: values.bankBranch[0].cityCode,
            openBank: values.opneBank.key,
            branchBank: values.bankBranch[0].bankCode,
            branchBankName: values.bankBranch[0].bankBranchName,
            attachmentOid: uploadOIDs.join(','),
          };
        } else {
          saveData = {
            ...model,
            id: model.id,
            employeeId: user.id,
            currencyCode: values.currencyCode,
            remarks: values.remarks,
            companyId: values.company[0].id,
            departmentId: values.company[0].departmentId || '1084803809249693697',
            openProvince: values.bankBranch[0].provinceCode,
            openCity: values.bankBranch[0].cityCode,
            openBank: values.opneBank.key,
            branchBank: values.bankBranch[0].bankCode,
            branchBankName: values.bankBranch[0].bankBranchName,
            attachmentOid: uploadOIDs.join(','),
          };
        }
        let method = accountService.insertHeader;
        if (!isNew) {
          method = accountService.updateHeader;
        }
        method(saveData)
          .then(res => {
            message.success('保存成功！');
            this.handleCancel('save');
            this.setState({ loading: false, isNew: false });
          })
          .catch(err => {
            message.error(err.response.data.message);
            this.setState({ loading: false });
          });
      });
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      user,
      company,
    } = this.props;
    const {
      loading,
      isNew,
      currencyList,
      fileList,
      model,
      openProvince,
      openCity,
      bankFetching,
      banknumber,
      bankOptions,
      approveStatus,
      isTrue,
    } = this.state;
    const formItemLayout = {
      labelCol: {
        span: 8,
      },
      wrapperCol: {
        span: 12,
      },
    };

    return (
      <div className="new-contract" style={{ marginBottom: 60, marginTop: 10 }}>
        <Form>
          {!isNew && (
            <FormItem label="单据编号" {...formItemLayout}>
              {getFieldDecorator('documentNumber', {
                initialValue: isNew ? '' : model.documentNumber,
              })(<Input disabled={true} />)}
            </FormItem>
          )}

          <FormItem label="申请公司" {...formItemLayout}>
            {getFieldDecorator('company', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew
                ? [{ id: company.id, name: company.name }]
                : [{ id: model.companyId, name: model.companyName }],
            })(
              <Chooser
                type="company"
                labelKey="name"
                valueKey="id"
                showClear={false}
                single={true}
                listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                disabled
              />
            )}
          </FormItem>

          <FormItem label="申请部门" {...formItemLayout}>
            {getFieldDecorator('department', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew
                ? [{ departmentId: user.departmentId, path: user.departmentName }]
                : [{ departmentId: model.departmentId, path: model.departmentName }],
            })(
              <Chooser
                type="department_document"
                labelKey="path"
                valueKey="departmentId"
                single
                listExtraParams={{ tenantId: user.tenantId }}
                disabled={isTrue}
              />
            )}
          </FormItem>

          <FormItem {...formItemLayout} label="开户银行">
            {getFieldDecorator('opneBank', {
              initialValue: isNew ? [] : [{ key: model.openBank, label: model.openBankName }],
            })(
              <Select labelInValue placeholder="请选择" disabled={isTrue} allowClear>
                {bankOptions.map(option => {
                  return <Option key={option.value}>{option.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>

          <FormItem label="分支行信息" {...formItemLayout}>
            {getFieldDecorator('bankBranch', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew
                ? []
                : [{ bankCode: model.branchBank, bankBranchName: model.branchBankName }],
            })(
              <Chooser
                placeholder={this.$t('common.please.select')}
                type="select_bank"
                labelKey="bankBranchName"
                valueKey="bankCode"
                onChange={this.chooseBank}
                single
                listExtraParams={{}}
                disabled={isTrue}
              />
            )}
          </FormItem>

          <FormItem label="联行号" {...formItemLayout}>
            {getFieldDecorator('banknumber', {
              rules: [{ required: true }],
              initialValue: banknumber.key
                ? banknumber
                : { key: model.branchBank, label: model.branchBank },
            })(<Select disabled labelInValue />)}
          </FormItem>

          <FormItem label="币种" {...formItemLayout}>
            {getFieldDecorator('currencyCode', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: isNew ? company.baseCurrency : model.currencyCode,
            })(
              <Select disabled={!isNew} onChange={this.currencyChange} disabled={isTrue} allowClear>
                {currencyList.map(item => {
                  return (
                    <Select.Option key={item.currency} value={item.currency}>
                      {item.currency}-{item.currencyName}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>

          <FormItem label="申请人" {...formItemLayout}>
            {getFieldDecorator('employeeName', {
              rules: [{ required: true }],
              initialValue: isNew ? user.userName : model.employeeName,
            })(<Input disabled={true} />)}
          </FormItem>

          <FormItem label="申请日期" {...formItemLayout}>
            {getFieldDecorator('requisitionDate', {
              rules: [{ required: true }],
              initialValue: isNew ? moment() : moment(model.requisitionDate),
            })(<DatePicker format="YYYY-MM-DD" disabled={isTrue} />)}
          </FormItem>

          <FormItem label="备注" {...formItemLayout}>
            {getFieldDecorator('remarks', {
              initialValue: isNew ? '' : model.remarks,
            })(<Input.TextArea autosize={{ minRows: 3 }} disabled={isTrue} allowClear />)}
          </FormItem>

          <FormItem label="附件信息" {...formItemLayout}>
            {getFieldDecorator('attachmentOID')(
              <Upload
                attachmentType="CONTRACT"
                uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                fileNum={9}
                uploadHandle={this.handleUpload}
                defaultFileList={fileList}
                defaultOIDs={isNew ? [] : model.attachmentOidList}
              />
            )}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.handleSave}
              // disabled={approveStatus === 'ZJ_APPROVED' || approveStatus === 'ZJ_PENGDING'}
            >
              保存
            </Button>
            <Button onClick={this.handleCancel}>取消</Button>
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

export default connect(map)(Form.create()(OpenAccountMaintain));
