import React, { Component } from 'react';
import { Form, Input, Button, message, Cascader, Select, Switch } from 'antd';

import { connect } from 'dva';

import 'styles/fund/account.scss';
import BSService from 'containers/basic-data/bank-definition/bank-definition.service';
import accountService from './csh_bank_numbers.service';

const FormItem = Form.Item;
const { Option } = Select;

class CshBankNumbersMaintain extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // 加载中
      isNew: true, // 是否为新建
      openBankList: [], // 开户银行值列表
      bank: [],
      countryData: [],
      //   banknumber: {
      //     key: '',
      //     label: '',
      //   }, // 联行号
      isTrue: false, // 单据状态为已审批和审批中的不可修改信息
      bankList: [], // 选择后返回的银行信息
      //  saveData:[], // 保存的数据
    };
  }

  componentDidMount() {
    const { params } = this.props;
    this.setState({
      bank: params,
      // country: params.country,
      countryDefaultValue: [params.provinceCode, params.cityCode],
      countryData: params.countryData ? params.countryData : [],
    });
    /* eslint-disable */
    // 省市下拉列表，默认北京-朝阳
    const countryDefaultValue = ['CHN011000000', 'CHN011005000'];
    if (params.params.countryCode === 'CHN000000000') {
      this.setState({
        isChina: true,
      });
    } else {
      this.setState({
        isChina: false,
      });
    }
    // 编辑
    if (typeof params.id !== 'undefined') {
      this.setState({
        countryDefaultValue: [params.provinceCode, params.cityCode],
        // countryDefaultValue: countryDefaultValue,
      });
    } else {
      this.setState({
        countryDefaultValue: countryDefaultValue,
      });
    }
    if (params.id) {
      this.setState({
        isNew: false,
      });
    }
    // 获取开户银行
    this.getSystemValueList('ZJ_OPEN_BANK')
      .then(res => {
        this.setState({
          openBankList: res.data.values,
        });
      })
      .catch(err => {
        message.error(err.message);
      });
  }

  componentWillReceiveProps(nextprops) {
    let params = nextprops.params;
    this.setState({
      bank: params,
      country: params.countryCode,
      countryDefaultValue: [params.provinceCode, params.cityCode],
      countryData: params.countryData ? params.countryData : [],
    });
  }

  insave = () => {
    const { save, form, user, params } = this.props;
    form.validateFields((err, values) => {
      const values1 = this.getRequestValue(values);
      if (!err) {
        const saveData = {
          id: params.id ? params.id : '',
          companyId: user.companyId || '',
          bankAddress: values.bankAddress || '',
          bankCode: values.bankCodeName.key || '',
          bankCodeName: values.bankCodeName.label || '',
          bankName: values.bankname || '',
          bankNumber: values.banknumber || '',
          cityCode: values.cityCode || '',
          provinceCode: values.provinceCode || '',
          countryCode: values.country || '',
          swiftCode: values.swiftCode || '',
          enabledFlag: values.enabledFlag ? 'Y' : 'N',
          // ifDisplay :true,
          // reviewStatus :true,
        };
        save(saveData);
      }
    });
  };

  onChange = values => {
    this.setState({
      isNew: true,
      bankList: values,
    });
  };

  /**
   * 点击选择银行
   */
  chooseBank = value => {
    const { form } = this.props;
    form.setFieldsValue({
      banknumber: {
        key: value[0].bankCode,
        label: value[0].bankCode,
      },
    });
  };

  /**
   * 取消
   */
  handleCancel = value => {
    const { onClose } = this.props;
    onClose(value);
  };

  // 选择国家值
  handleCountryChange = value => {
    const { form } = this.props;
    // 选择国家时把开户地置空
    form.setFieldsValue({
      openAccount: '',
    });
    if (value === 'CHN000000000') {
      this.setState({
        isChina: true,
      });
    } else {
      this.setState({
        isChina: false,
      });
    }
  };
  // 渲染国家的选项

  renderCountryOption = data => {
    return data.map(item => {
      return (
        <Option value={item.value} key={item.code}>
          {item.label}
        </Option>
      );
    });
  };

  // 上传之前，表单的值需要处理
  getRequestValue = values => {
    if (this.state.isChina) {
      values.countryCode = values.country;
      values.countryName = BSService.getCountryNameByCode(
        values.countryCode,
        this.state.countryData
      );
      if (values.openAddress.length > 0) {
        values.provinceCode = values.openAddress[0];
        values.province = BSService.getStateNameByCode(
          values.countryCode,
          values.provinceCode,
          this.state.countryData
        );
      }
      if (values.openAddress.length > 1) {
        values.cityCode = values.openAddress[1];
        values.city = BSService.getCityNameByCode(
          values.countryCode,
          values.provinceCode,
          values.cityCode,
          this.state.countryData
        );
      }

      return values;
    } else {
      values.countryCode = values.country;
      values.countryName = BSService.getCountryNameByCode(
        values.countryCode,
        this.state.countryData
      );
      values.provinceCode = '';
      values.cityCode = '';
      values.province = '';
      values.city = '';
      return values;
    }
  };

  // 根据是否是中国，渲染开户地
  renderOpenAccountByChina = () => {
    const { form, params } = this.props;
    const { bankTypeHelp, bank, countryData, countryDefaultValue, isChina } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 1 },
    };
    let openAccountDom = '';
    if (isChina) {
      /* 省市:只有中国时才联动 */
      openAccountDom = (
        <FormItem {...formItemLayout} label={this.$t('bank.openAccount')} help={bankTypeHelp}>
          {form.getFieldDecorator('openAddress', {
            initialValue: countryDefaultValue || '',
          })(
            <Cascader
              options={accountService.getCountryDataByCode('CHN000000000', countryData)}
              onChange={this.onStateChange}
              placeholder={this.$t('common.please.select')}
            />
          )}
        </FormItem>
      );
    } else {
      /* 开户地 */
      openAccountDom = (
        <FormItem {...formItemLayout} label={this.$t('bank.openAccount')} help={bankTypeHelp}>
          {form.getFieldDecorator('openAddress', {
            initialValue: params.params.openAddress || '',
          })(<Input placeholder={this.$t('common.please.enter')} />)}
        </FormItem>
      );
    }

    return openAccountDom;
  };

  render() {
    const {
      form: { getFieldDecorator },
      params,
    } = this.props;
    const { loading, isNew, bankList, isTrue, country, countryData, openBankList } = this.state;
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
          {/* 国家 */}
          <FormItem {...formItemLayout} label={this.$t('bank.country')}>
            {getFieldDecorator('country', {
              initialValue: params.params.countryName || '',
            })(
              <Select
                allowClear="true"
                className="select-country"
                showSearch
                placeholder={this.$t('common.please.select')}
                optionFilterProp="children"
                onChange={this.handleCountryChange}
                filterOption={(input, option) =>
                  option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                }
              >
                {this.renderCountryOption(countryData)}
              </Select>
            )}
          </FormItem>

          <FormItem label="银行名称" {...formItemLayout}>
            {getFieldDecorator('bankCodeName', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: { key: params.params.bankCode, label: params.params.bankCodeName },
            })(
              <Select labelInValue placeholder="请选择" allowClear>
                {openBankList.map(option => {
                  return <Option key={option.value}>{option.name}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem label="联行号" {...formItemLayout}>
            {getFieldDecorator('banknumber', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: params.params.bankNumber || '',
            })(<Input />)}
          </FormItem>
          <FormItem label="分支行名称" {...formItemLayout}>
            {getFieldDecorator('bankname', {
              rules: [{ required: true, message: this.$t('common.please.select') }],
              initialValue: params.params.bankName || '',
            })(<Input />)}
          </FormItem>

          {// 渲染开户地，中国是联动
          this.renderOpenAccountByChina()}
          <FormItem label="Swift Sode" {...formItemLayout}>
            {getFieldDecorator('swiftCode', {
              rules: [{ required: true }],
              initialValue: params.params.swiftCode,
            })(<Input disabled={isTrue} />)}
          </FormItem>

          <FormItem label="详细地址" {...formItemLayout}>
            {getFieldDecorator('bankAddress', {
              initialValue: params.params.bankAddress,
            })(<Input disabled={isTrue} />)}
          </FormItem>
          <FormItem label="状态" {...formItemLayout}>
            {getFieldDecorator('enabledFlag', {
              valuePropName: 'checked',
              initialValue: params.params.enabledFlag === 'Y',
            })(<Switch checkedChildren="启用" unCheckedChildren="未启用" />)}
          </FormItem>
          <div className="slide-footer">
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ margin: '0 20px' }}
              onClick={this.insave}
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

export default connect(
  map,
  null,
  null,
  { withRef: true }
)(Form.create()(CshBankNumbersMaintain));
