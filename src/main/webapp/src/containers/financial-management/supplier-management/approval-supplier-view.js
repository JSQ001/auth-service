import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Upload from 'widget/upload';
import {
  Button,
  Input,
  Select,
  Switch,
  DatePicker,
  Icon,
  Badge,
  message,
  Form,
  Row,
  Col,
} from 'antd';
import Table from 'widget/table';
import vendorService from 'containers/financial-management/supplier-management/vendorService';
import baseService from 'share/base.service';
import config from 'config';
import Importer from 'components/Widget/Template/importer';
import 'styles/financial-management/supplier-management/new-update-supplier.scss';
import Chooser from 'components/Widget/chooser';
import { get } from 'https';

const { TextArea } = Input;
const FormItem = Form.Item;
const Option = Select.Option;

class NewUpdateSupplier extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      loading: false,
      enabled: true,
      firstRender: true,
      isEdit: false,
      model: {},
      vendorInfo: {
        venType: [],
      },
      basicInfo: [
        {
          type: 'input',
          flag: 'basic',
          // isRequired: true,
          label: '申请人', //申请人
          key: 'user',
          disabled: true,
        },
        {
          type: 'chooser',
          flag: 'basic',
          isRequired: true,
          label: this.$t('supplier.management.type'),
          key: 'venTypes', //供应商类型
          valueKey: 'id',
          labelKey: 'name',
          listType: 'vendor_type',
          single: true,
          listExtraParams: { enabled: true },
          disabled: true,
        },
        {
          type: 'select',
          flag: 'basic',
          isRequired: true,
          label: '供应商类别',
          key: 'venVendorIndustryInfoList', //供应商类别
          options: [],
          labelKey: 'messageKey',
          valueKey: 'id',
          method: this.getSystemValueList(2214),
          disabled: true,
        },
        {
          type: 'input',
          flag: 'basic',
          isRequired: true,
          label: this.$t('supplier.management.code'),
          key: 'venderCode',
          disabled: true,
        }, //供应商代码
        {
          type: 'input',
          flag: 'basic',
          isRequired: true,
          label: this.$t('supplier.management.name'),
          key: 'venNickname',
          disabled: true,
        }, //供应商名称

        {
          type: 'input',
          flag: 'basic',
          isRequired: true,
          label: this.$t('supplier.management.taxNumber'),
          key: 'taxIdNumber',
          disabled: true,
        },
        {
          type: 'input',
          flag: 'basic',
          isRequired: false,
          label: this.$t('supplier.management.legalRepresentative'),
          key: 'artificialPerson',
          disabled: true,
        },
        //备注
        {
          type: 'textArea',
          isRequired: false,
          label: this.$t('supplier.management.remark'),
          key: 'notes',
          disabled: true,
        },
        {
          type: 'date',
          flag: 'basic',
          isRequired: false,
          label: this.$t('supplier.management.commissionDate'),
          key: 'effectiveDate',
          disabled: true,
        },
        //启用日期
        // { type: 'input', flag: 'basic', isRequired: false, label: this.$t('supplier.management.outerId'), key: 'venNickOid' }, //外部标识id
        {
          type: 'switch',
          flag: 'basic',
          isRequired: false,
          label: this.$t('common.column.status'),
          key: 'venType',
          disabled: true, //状态
        },
      ],
      otherInfo: [
        // {
        //   type: 'select',
        //   flag: 'other',
        //   isRequired: false,
        //   label: this.$t("supplier.management.industryCategories"),
        //   key: 'industryName',//行业类别
        //   options: [],
        //   labelKey: 'messageKey',
        //   valueKey: 'id',
        //   method: this.getSystemValueList(2214)
        // },                                                                       //供应商评级
        // {
        //   type: 'select',
        //   flag: 'other',
        //   isRequired: false,
        //   label: this.$t("supplier.management.rateLevel"),
        //   key: 'venderLevelName',
        //   options: [],
        //   labelKey: 'messageKey',
        //   valueKey: 'id',
        //   method: this.getSystemValueList(2215)
        // },                                                                      //法人代表

        //联系人
        {
          type: 'input',
          flag: 'other',
          isRequired: false,
          label: this.$t('supplier.management.person'),
          key: 'contact',
          disabled: true,
        },
        //联系人电话
        {
          type: 'input',
          flag: 'other',
          isRequired: false,
          label: this.$t('supplier.management.personPhone'),
          key: 'contactPhone',
          disabled: true,
        },
        //邮箱
        {
          type: 'input',
          flag: 'other',
          isRequired: false,
          label: this.$t('supplier.management.main'),
          key: 'contactMail',
          disabled: true,
        },
        //传真
        {
          type: 'input',
          flag: 'other',
          isRequired: false,
          label: this.$t('supplier.management.facsimile'),
          key: 'fax',
          disabled: true,
        },
        //国家
        // {
        //   type: 'select',
        //   flag: 'other',
        //   isRequired: false,
        //   label: this.$t("supplier.management.country"),
        //   key: 'country',
        //   options: [],
        //   labelKey: 'country',
        //   valueKey: 'code',
        //   //method: baseService.getCountries({language: this.props.language.locale ==='zh' ? "zh_cn" : "en_us", page: 0, size: 9999}),
        // },
        //地址
        {
          type: 'textArea',
          isRequired: false,
          label: this.$t('supplier.management.address'),
          key: 'address',
          disabled: true,
        },
      ],
    };
  }

  componentDidMount() {
    let params = { ...this.props.params };

    if (typeof params.id !== 'undefined') {
      //编辑
      let basicInfo = this.state.basicInfo;
      basicInfo[0].disabled = true;
      basicInfo[1].disabled = true;
      this.props.form.setFieldsValue({
        venTypes: [
          {
            id: params.venderTypeId,
            name: params.venderTypeName,
            vendorTypeCode: params.vendorTypeCode,
          },
        ],
      });
      this.setState({
        vendorInfo: params,
        basicInfo,
        isEdit: true,
        enabled: params.venType === 1001 ? true : false,
      });
    }

    //获取国家
    // let params = { language: this.props.language.code === 'zh_cn' ? "zh_cn" : "en_us", page: 0, size: 9999 };
    // baseService.getCountries(params).then((response) => {
    //   let country = [];
    //   response.data.map((item) => {
    //     let option = {
    //       label: item.country,
    //       key: item.code + "-" + item.country
    //     };
    //     country.push(option)
    //   });
    //   let otherInfo = this.state.otherInfo;
    //   otherInfo[8].options = country;
    //   this.setState({
    //     country,
    //     otherInfo
    //   })
    // });
    // .catch(e => {
    //   if (e.response) {
    //     message.error(`保存失败,${e.response.data.message}`);
    //   }
    //   this.setState({ loading: false });
    // });
  }
  // getUser() {
  //   console.log(this.props.user);
  //   console.log(this.props.user.userName);
  //   console.log(this.props.user.userOid);
  //   console.log(this.state);
  //   let user = [];
  //   let option = {
  //     label: this.props.user.userName,
  //     key: this.props.user.userOid
  //   };
  //   user.push(option);
  //   let basicInfo = this.state.basicInfo;
  //   console.log(basicInfo);
  //   basicInfo[0].options = user;
  //   this.setState({
  //     user,
  //     basicInfo
  //   })
  // };

  handleChange = key => {
    /* switch (key){
     case 'venType':{
     this.setState((prevState) => ({
     enabled: !prevState.enabled
     }))
     }
     }*/
  };

  switchChange = () => {
    this.setState(prevState => ({
      enabled: !prevState.enabled,
    }));
  };
  //上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };
  getOptions = item => {
    item.method.then(response => {
      let options = [];
      response.data.values.map(data => {
        options.push({
          label: item.renderOption ? item.renderOption(data) : data[item.labelKey],
          key: data[item.valueKey],
        });
      });
      if (item.flag === 'basic') {
        let basicInfo = this.state.basicInfo;
        basicInfo.map(searchItem => {
          if (searchItem.key === item.key) {
            searchItem.options = options;
          }
        });
        this.setState({
          basicInfo,
        });
      } else {
        let otherInfo = this.state.otherInfo;
        otherInfo.map(searchItem => {
          if (searchItem.key === item.key) {
            searchItem.options = options;
          }
        });
        this.setState({
          otherInfo,
        });
      }
    });
  };

  renderFormItem(item) {
    switch (item.type) {
      //输入组件
      case 'input': {
        return (
          <Input
            placeholder={this.$t('common.please.enter')}
            onChange={this.handleChange(item.key)}
            disabled={item.disabled}
          />
        );
      }
      //选择组件
      case 'select': {
        if (item.key == 'venVendorIndustryInfoList') {
          //供应商类别
          return (
            <Select
              mode="multiple"
              placeholder={this.$t('common.please.select')}
              onChange={this.handleChange(item.key)}
              allowClear
              showSearch
              disabled={item.disabled}
              labelInValue={!!item.entity}
              onFocus={
                item.options.length === 0 && item.method ? () => this.getOptions(item) : () => {}
              }
            >
              {item.options.map(option => {
                return (
                  <Option key={option.key} value={option.key}>
                    {option.label}
                  </Option>
                );
              })}
            </Select>
          );
        } else {
          return (
            <Select
              placeholder={this.$t('common.please.select')}
              onChange={this.handleChange(item.key)}
              allowClear
              showSearch
              disabled={item.disabled}
              labelInValue={!!item.entity}
              onFocus={
                item.options.length === 0 && item.method ? () => this.getOptions(item) : () => {}
              }
            >
              {item.options.map(option => {
                return (
                  <Option key={option.key} value={option.key}>
                    {option.label}
                  </Option>
                );
              })}
            </Select>
          );
        }
      }
      //值列表选择组件
      //switch状态切换组件
      case 'switch': {
        return (
          <div>
            <Switch
              checkedChildren={<Icon type="check" />}
              unCheckedChildren={<Icon type="cross" />}
              checked={this.state.enabled}
              onChange={this.switchChange}
              disabled={item.disabled}
            />
            <span className="enabled-type" style={{ marginLeft: 20, width: 100 }}>
              {this.state.enabled ? this.$t('common.status.enable') : this.$t('common.disabled')}
            </span>
          </div>
        );
      }
      //日期组件
      case 'date': {
        return <DatePicker format="YYYY-MM-DD" disabled={item.disabled} />;
      }
      case 'textArea': {
        return <TextArea disabled={item.disabled} placeholder={this.$t('common.please.enter')} />;
      }
      case 'chooser': {
        return (
          <Chooser
            placeholder={this.$t('common.please.select')}
            type={item.listType}
            valueKey={item.valueKey}
            labelKey={item.labelKey}
            disabled={item.disabled}
            listExtraParams={item.listExtraParams}
            single={item.single}
          />
        );
      }
    }
  }

  getFields = array => {
    const { getFieldDecorator } = this.props.form;
    const { vendorInfo, isEdit } = this.state;
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 0 },
    };
    let children = [];

    array.map(item => {
      let rules = [
        {
          required: item.isRequired,
          message: this.$t('common.can.not.be.empty', { name: item.label }), //name 不可为空
        },
      ];
      if (item.key == 'address' || item.key == 'notes') {
        rules.push({
          max: 200,
          message: '不能超过200字',
        });
      }
      children.push(
        <Row gutter={22} key={item.key}>
          <Col span={22}>
            <FormItem {...formItemLayout} label={item.label}>
              {getFieldDecorator(item.key, {
                valuePropName: item.type === 'switch' ? 'checked' : 'value',
                initialValue:
                  isEdit && vendorInfo[item.key]
                    ? item.type === 'switch'
                      ? this.state.enabled
                      : vendorInfo[item.key]
                    : null,
                rules: rules,
              })(this.renderFormItem(item))}
            </FormItem>
          </Col>
        </Row>
      );
    });
    return children;
  };

  handleSubmit = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.setState({ loading: true });

        values.venType = this.state.enabled ? 1001 : 1002;
        values.employeeID = this.props.user.employeeID;
        values.venderTypeId = values.venTypes[0].id;
        values.industryId = values.industryName;
        values.venderLevelId = values.venderLevelName;

        // values.effectiveDate = new Date(values.effectiveDate.format('YYYY-MM-DD')).getTime();
        if (values.effectiveDate && values.effectiveDate.format) {
          values.effectiveDate = new Date(values.effectiveDate.format('YYYY-MM-DD')).getTime();
        } else {
          values.effectiveDate = null;
        }

        values.venOperatorNumber = this.props.user.employeeID;
        values.venOperatorName = this.props.user.fullName;
        let method = null;
        let source = 'TENANT';
        values.source = source;
        if (this.state.isEdit) {
          let vendorInfo = this.state.vendorInfo;
          values.id = vendorInfo.id;
          values.createTime = vendorInfo.createTime;
          if (vendorInfo.venderLevelId !== null) {
            if (typeof values.venderLevelName !== 'undefined') {
              if (vendorInfo.venderLevelName === values.venderLevelName) {
                values.venderLevelId = vendorInfo.venderLevelId;
              }
            }
          }
          if (vendorInfo.industryId !== null) {
            if (typeof values.industryName !== 'undefined') {
              if (vendorInfo.industryName === values.industryName) {
                values.industryId = vendorInfo.industryId;
              }
            }
          }
          method = vendorService.updateVendorInfo(values);
        } else {
          values.industryId = values.industryName;
          values.venderLevelId = values.venderLevelName;
          method = vendorService.addVendorInfo(values);
        }

        method
          .then(response => {
            //this.props.form.resetFields();
            this.setState({ loading: false });
            this.props.onClose(true);
            message.success(
              `${this.$t(
                !this.state.isEdit
                  ? ('common.save.success', { name: '' })
                  : 'common.operate.success'
              )}`
            );
          })
          .catch(e => {
            message.error(
              `${this.$t(!this.state.isEdit ? 'common.save.filed' : 'common.operate.filed')}, ${
                e.response.data.message
              }`
            );
            this.setState({ loading: false });
          });
      }
    });
  };
  submitSupplier = str => {
    vendorService
      .operationSupplier(this.state.vendorInfo, str)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('common.operate.success' /*操作成功*/));
          this.setState({ loading: false });
          this.props.onClose(true);
        }
      })
      .catch(e => {
        this.setState({ loading: false });
        message.error(`${this.$t('exp.summit.failed')}，${e.response.data.message}`);
      });
  };
  handleCancel = () => {
    this.props.onClose(false);
  };
  render() {
    const { basicInfo, otherInfo, loading, fileList, isNew, user, model } = this.state;
    const { getFieldDecorator } = this.props.form;

    const rowLayout = { type: 'flex', gutter: 48, justify: 'center' };
    const formItemLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14, offset: 0 },
    };

    return (
      <div className="new-update-supplier">
        <Form onSubmit={this.handleSubmit}>
          <div className="new-update-supplier-basic">
            <div className="basic-icon" />
            <div className="basic-title">{this.$t('supplier.management.basicInfo')}</div>

            <div className="basic-content">{this.getFields(basicInfo)}</div>

            <Row {...rowLayout} style={{ marginBottom: 40, marginLeft: 20 }}>
              <Col span={10}>
                <FormItem label="附件信息" {...formItemLayout}>
                  {getFieldDecorator('attachmentOID')(
                    <Upload
                      attachmentType="SUPPLIER_MANAGEMENT"
                      uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                      fileNum={9}
                      uploadHandle={this.handleUpload}
                      defaultFileList={fileList}
                      defaultOids={isNew ? [] : model.attachmentOidList}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
          </div>
          <div className="new-update-supplier-other">
            <div className="other-icon" />
            <div className="other-title">{this.$t('supplier.management.otherInfo')}</div>
            <div className="other-content">{this.getFields(otherInfo)}</div>
          </div>
          <div className="slide-footer">
            {/* <Button type="primary" htmlType="submit" loading={loading}>
              {this.$t('common.save')}
            </Button> */}
            <Button
              type="primary"
              onClick={this.submitSupplier.bind(this, 'APPROVED')}
              loading={loading}
            >
              同意
            </Button>
            <Button
              type="primary"
              onClick={this.submitSupplier.bind(this, 'REFUSE')}
              loading={loading}
            >
              拒绝
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    language: state.languages.languages,
    user: state.user.currentUser,
  };
}
const WrappedNewUpdateSupplier = Form.create()(NewUpdateSupplier);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewUpdateSupplier);
