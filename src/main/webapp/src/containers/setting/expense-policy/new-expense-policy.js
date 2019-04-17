import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import SelectApplicationType from 'widget/select-application-type';
import Selector from 'components/Widget/selector';
import Chooser from 'components/Widget/chooser';
import 'styles/setting/expense-policy/new-expense-policy.scss';
import { renderFormItem, getFormItemDefaultValue, setDynamicValue } from 'utils/utils';
import {
  Button,
  Form,
  Select,
  Input,
  InputNumber,
  Row,
  Col,
  DatePicker,
  Switch,
  message,
  Radio,
  Spin,
  Popover,
  Icon,
  Affix,
} from 'antd';
import expensePolicyService from 'containers/setting/expense-policy/expense-policy.service';
import moment from 'moment';
const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

class NewExpensePolicy extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      startValue: null,
      endValue: null,
      setOfBooks: [],
      dynamicValueObj: {},
      detailVisible: false,
      record: {
        expenseType: {},
      },
      currencyOptions: [], // 币种
      dutyTypeOptions: [], // 职务类型
      staffLevelOptions: [], // 员工级别
      controlStrategyOptions: [], // 控制策略
      dynamicControlDimensionOptions: [], //动态控制维度
      baseControlDimensionOptions: [], //基础控制维度
      controlDimensionConditions: [], //控制维度条件列表
      controlRule: {}, //控制规则
      messageOptions: [], // 费用政策消息
      expenseTypeInfo: {}, //费用项目信息
      controlDimension: {}, //控制维度
      chooseSetOfBooksId: 0, //选择的账套id
      chooseCompanyScope: 1, //选择适用公司
      relatedCompanies: [], //表单-适用公司
      expenseTypeId: 0, //表单-申请项目id
      currencyCode: '', //表单-币种代码
      dynamicFields: [], //表单-动态字段

      formList1: [
        {
          id: 'setOfBooksId',
          label: this.$t('expense.policy.setOfBooksName'),
          type: 'select',
          required: true,
          disabled: true,
          entity: true,
        },
        {
          id: 'priority',
          label: this.$t('expense.policy.priority'),
          type: 'inputNumber',
          required: true,
          validator: (item, value, callback) => {
            if (value != null) {
              var regu = /^\+?[1-9][0-9]*$/;
              if (!regu.test(value)) {
                callback(this.$t('只允许输入正整数!'));
              }
              callback();
            }
          },
        },
        {
          id: 'expenseType',
          label:
            props.match.params.typeFlag === '0'
              ? this.$t('expense.policy.expenseTypeName')
              : '报销项目',
          type: 'expense',
          required: true,
          defaultValue: {},
          method: `${
            config.expenseUrl
          }/api/expense/application/type/query/expense/type/by/setOfBooksId`,
          params: {
            setOfBooksId: props.match.params.setOfBooksId,
            typeFlag: props.match.params.typeFlag,
          },
          validate: true,
        },
      ],
      formList2: [
        {
          id: 'companyLevelId',
          label: this.$t('expense.policy.companyLevelName'),
          type: 'selector',
          selectorItem: {
            url: `${config.mdataUrl}/api/companyLevel/selectByTenantId`,
            label: record => `${record.description}`,
            key: 'id',
          },
        },
        {
          id: 'dutyType',
          label: this.$t('expense.policy.dutyName'),
          type: 'select',
          listKey: 'values',
          labelKey: 'name',
          entity: true,
          valueKey: 'value',
          method: () => this.getSystemValueList(1002),
        },
        {
          id: 'staffLevel',
          label: this.$t('expense.policy.staffLevelName'),
          type: 'select',
          listKey: 'values',
          entity: true,
          labelKey: 'name',
          valueKey: 'value',
          method: () => this.getSystemValueList(1008),
        },
        {
          id: 'departmentId',
          label: this.$t('expense.policy.departmentName'),
          type: 'chooser',
          single: true,
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
        },
        {
          id: 'currencyCode',
          label: this.$t('expense.policy.currencyName'),
          type: 'select',
          required: true,
          listKey: 'records',
          entity: true,
          labelKey: 'currencyCodeAndName',
          valueKey: 'currencyCode',
          method: expensePolicyService.getCurrency,
          params: {
            enable: true,
            tenantId: props.company.tenantId,
            setOfBooksId: props.match.params.setOfBooksId,
          },
        },
      ],
      //公司级别下拉单
      selectListCompanyLevel: {
        url: `${config.mdataUrl}/api/companyLevel/selectByTenantId`,
        label: record => `${record.description}`,
        key: 'id',
      },
    };
  }

  componentWillMount() {
    expensePolicyService.getTenantAllSob().then(res => {
      const { formList1 } = this.state;
      let sob = res.data.find(item => item.id === this.props.match.params.setOfBooksId);
      formList1[0].defaultValue = {
        key: sob.id,
        label: sob.setOfBooksCode + '-' + sob.setOfBooksName,
      };
      this.setState({ setOfBooks: res.data, formList1 });
    });

    //获取控制策略值列表
    this.getControlStrategy();
    //获取控制维度值列表
    this.getControlDimensionType();
    //获取费用政策消息值列表
    this.getExpensePolicyMessage();
    //获取控制条件值列表
    this.getControlDimensionCondition();
    if (this.props.match.params.id || this.props.match.params.new !== 'new') {
      this.getExpensePolicy();
    }
  }

  //获取行信息
  getExpensePolicy() {
    expensePolicyService
      .getExpensePolicyById(this.props.match.params.id || this.props.match.params.new)
      .then(res => {
        let { setOfBooks, controlDimensionConditions } = this.state;
        let record = { ...res.data };

        let sob = setOfBooks.find(item => item.id === record.setOfBooksId);
        this.getDynamicControlDimensionOptions(record.expenseTypeInfo.fields);
        record.dynamicFields &&
          record.dynamicFields.map(item => {
            if (item.fieldType === 'START_DATE_AND_END_DATE' && item.value) {
              record[item.fieldId] = moment(item.value);
            } else {
              record[item.fieldId] = item.value;
            }

            // item.fieldType = 'TEXT';
          });

        const detail = this.props.match.params.detail === 'expense-policy-detail';

        //设置默认值
        record.expenseType = detail ? record.expenseTypeName : record.expenseTypeInfo;
        //record.fields = record.expenseTypeInfo.fields;
        record.companyLevelId &&
          (record.companyLevelId = detail
            ? record.companyLevelName
            : { key: record.companyLevelId, label: record.companyLevelName });
        record.dutyType = detail
          ? record.dutyTypeName
          : { key: record.dutyType, label: record.dutyTypeName };
        record.staffLevel = detail
          ? record.staffLevelName
          : { key: record.staffLevel, label: record.staffLevelName };
        record.currencyCode = detail
          ? record.currencyCode + '-' + record.currencyName
          : { key: record.currencyCode, label: record.currencyCode + '-' + record.currencyName };
        detail
          ? (record.departmentId = record.departmentName)
          : record.departmentId &&
            (record.departmentId = [
              { departmentId: record.departmentId, name: record.departmentName },
            ]);
        record.setOfBooksId = detail
          ? record.setOfBooksName
          : {
              key: record.setOfBooksId,
              label: sob.setOfBooksCode + '-' + record.setOfBooksName,
            };
        record.controlStrategyCode = detail
          ? record.controlStrategyName
          : {
              key: record.controlStrategyCode,
              label: record.controlStrategyName,
            };
        if (detail) {
          let dim = record.controlDimensionType;
          let opt =
            record.judgementSymbol &&
            controlDimensionConditions.find(item => record.judgementSymbol === item.value).name;
          let val = record.controlDimensions[0].value;
          record.options = dim + opt + val;
        }
        let fields = this.getRenderDynamic(record.expenseTypeInfo.fields);
        this.setState(
          {
            record,
            expenseTypeInfo: {
              ...record.expenseTypeInfo,
              fields,
            },
            chooseSetOfBooksId: res.data.setOfBooksId,
            chooseCompanyScope: res.data.allCompanyFlag ? 1 : 2,
            relatedCompanies: res.data.relatedCompanies
              ? res.data.relatedCompanies.map(item => {
                  return { id: item.companyId, name: item.companyName };
                })
              : [],
          },
          () => {
            let { record } = this.state;
            let detail = this.props.match.params.detail === 'expense-policy-detail';
            setDynamicValue(record.dynamicFields, detail).then(res => {
              console.log(res);
              record.dynamicFields = res;
              this.setState({ record });
            });
          }
        );
      });
  }

  //获取控制策略值列表
  getControlStrategy = () => {
    //如果已经有值，则不再查询
    if (this.state.controlStrategyOptions != '' && this.state.controlStrategyOptions != undefined) {
      return;
    }
    this.getSystemValueList('CONTROL_STRATEGY').then(res => {
      //控制策略
      let controlStrategyOptions = res.data.values || [];
      this.setState({ controlStrategyOptions });
    });
  };

  //获取控制维度值列表
  getControlDimensionType = () => {
    //如果已经有值，则不再查询
    if (
      this.state.baseControlDimensionOptions != '' &&
      this.state.baseControlDimensionOptions != undefined
    ) {
      return;
    }
    this.getSystemValueList('CONTROL_DIMENSION').then(res => {
      //控制维度
      let baseControlDimensionOptions = res.data.values || [];
      this.setState({
        dynamicControlDimensionOptions: baseControlDimensionOptions,
        baseControlDimensionOptions: baseControlDimensionOptions,
      });
    });
  };

  //获取费用政策消息值列表
  getExpensePolicyMessage = () => {
    //如果已经有值，则不再查询
    if (this.state.messageOptions != '' && this.state.messageOptions != undefined) {
      return;
    }
    this.getSystemValueList('EXPENSE_POLICY_MESSAGE').then(res => {
      //费用政策消息
      let messageOptions = res.data.values || [];
      this.setState({ messageOptions });
    });
  };

  //获取控制维度条件列表
  getControlDimensionCondition = () => {
    //如果已经有值，则不再查询
    if (this.state.messageOptions != '' && this.state.messageOptions != undefined) {
      return;
    }
    this.getSystemValueList('exp_condition').then(res => {
      //控制维度条件
      let controlDimensionConditions = res.data.values || [];
      this.setState({ controlDimensionConditions });
    });
  };

  //点击取消，返回
  handleCancel = e => {
    e.preventDefault();
    const { setOfBooksId, typeFlag } = this.props.match.params;
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/expense-policy/expense-policy/${setOfBooksId}/${typeFlag}`,
      })
    );
  };

  getControlDimensions(params) {
    let type = this.props.form.getFieldValue('controlDimensionType');
    let value = null;
    switch (type.toString()) {
      case '10001':
      case '10002':
      case '20001':
      case '30001':
      case '20002':
        {
          value = params[0].id;
        }
        break;
      case '10003':
      case '20003':
        {
          value = params[0].departmentOid;
        }
        break;
      default:
        value = params;
        break;
    }
    return [{ value: value }];
  }

  //保存
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        let { relatedCompanies, chooseCompanyScope } = this.state;
        chooseCompanyScope = chooseCompanyScope > 1 ? 0 : 1;
        if (!chooseCompanyScope && (relatedCompanies && !relatedCompanies.length)) {
          message.warning('请至少选择一个公司');
          return;
        }
        let controlDimensions = this.getControlDimensions(values.controlDimensionValue);

        values = {
          expenseTypeId: values.expenseType.id,
          controlDimensions,
          allCompanyFlag: chooseCompanyScope,
          ...values,
        };
        values.currencyCode = values.currencyCode.key;
        values.controlStrategyCode = values.controlStrategyCode.key;
        values.dutyType && (values.dutyType = values.dutyType.key);
        values.staffLevel && (values.staffLevel = values.staffLevel.key);
        values.departmentId && values.departmentId.length > 0
          ? (values.departmentId = values.departmentId[0].departmentId)
          : (values.departmentId = null);
        values.setOfBooksId = values.setOfBooksId.key;
        values.enabled = !!values.enabled;
        values.companyLevelId != null &&
          values.companyLevelId.key != null &&
          (values.companyLevelId = values.companyLevelId.key);
        this.state.record.id && (values.id = this.state.record.id);
        // this.setState({ loading: true });
        values.dynamicFields =
          this.state.expenseTypeInfo.fields &&
          this.state.expenseTypeInfo.fields.map(item => {
            console.log(item);
            if (item.fieldType === 'START_DATE_AND_END_DATE' && values[item.id]) {
              return {
                fieldId: item.id,
                fieldType: item.fieldType,
                name: item.name,
                value: null,
                expensePolicyFieldProperty: {
                  dateTime1: values[item.id][0],
                  dateTime2: values[item.id][1],
                },
              };
            } else {
              let value = null;
              if (item.valueKey && values[item.id] && values[item.id].length) {
                value = item.valueKey
                  ? values[item.id]
                    ? values[item.id][0][item.valueKey]
                    : ''
                  : values[item.id];
              }
              return {
                fieldId: item.id,
                fieldType: item.fieldType,
                name: item.name,
                value: value,
              };
            }
          });
        values.relatedCompanies =
          this.state.relatedCompanies &&
          this.state.relatedCompanies.map(item => {
            return { companyId: item.id, companyName: item.name };
          });

        let method = 'post';
        if (this.props.match.params.id) {
          method = 'put';
          values.id = this.state.record.id;
          values.versionNumber = this.state.record.versionNumber;
        }
        console.log(values);
        !!this.props.match.params.new && delete values.id;
        expensePolicyService
          .saveExpensePolicy(method, values)
          .then(res => {
            if (res.status === 200) {
              message.success(this.$t({ id: 'common.save.success' }, { name: '' } /*保存成功*/));
              const { setOfBooksId, typeFlag } = this.props.match.params;
              this.props.dispatch(
                routerRedux.push({
                  pathname: `/admin-setting/expense-policy/expense-policy-detail/${
                    res.data.id
                  }/${setOfBooksId}/${typeFlag}`,
                })
              );
              this.setState({ loading: false });
            }
          })
          .catch(e => {
            this.setState({ loading: false });
            message.error(
              `${this.$t({ id: 'common.save.filed' } /*保存失败*/)}, ${e.response.data.message}`
            );
          });
      }
    });
  };
  //渲染动态组件

  //获取动态控制维度
  getDynamicControlDimensionOptions = extraOptions => {
    if (!extraOptions) return;
    const { baseControlDimensionOptions } = this.state;
    let dynamicControlDimensionOptions = [];
    extraOptions
      .filter(item => !['PARTICIPANT', 'PARTICIPANTS', 'GPS'].includes(item.fieldType))
      .map(item => {
        item.value = item.id;
        /*item.id === '10001'&& (item.value = 10001)
        item.id === '10002'&& (item.value = 10002)
        item.id === '10003'&& (item.value = 10003)
        item.id === '20001'&& (item.value = 20001)
        item.id === '20002'&& (item.value = 20002)
        item.id === '20003'&& (item.value = 20003)
        item.id === '30001'&& (item.value = 30001)*/
        dynamicControlDimensionOptions.push({ ...item });
      });
    this.setState({
      dynamicControlDimensionOptions: baseControlDimensionOptions.concat(
        dynamicControlDimensionOptions
      ),
    });
  };

  //选择同行人职务，级别设置可选值
  setValue = () => {};

  //选择控制维度
  selectControlDimension = value => {
    console.log(value);
    const { dynamicControlDimensionOptions } = this.state;
    let controlDimension = dynamicControlDimensionOptions.find(item => item.value === value);
    controlDimension.type = controlDimension.fieldType;
    console.log(controlDimension);
    this.setState({ controlDimension });
  };

  validate = (item, value, callback) => {
    if (!value.id) {
      let label =
        this.props.match.params.typeFlag === '0'
          ? this.$t('expense.policy.expenseTypeName')
          : '报销项目';
      callback(label + '不能为空');
    }
    callback();
  };

  //渲染动态组件
  renderControlDimension = item => {
    const { getFieldDecorator } = this.props.form;
    const type = item.fieldType ? item.fieldType : 'DEFAULT';
    switch (type) {
      case 'TEXT':
        return <Input />;
      case 'CUSTOM_ENUMERATION':
        return (
          <Select>
            {item.options &&
              item.options.map(o => {
                return <Select.Option key={o.value}>{o.label}</Select.Option>;
              })}
          </Select>
        );
      case 'DEFAULT':
        return <InputNumber />;
    }
  };

  //生成控制规则
  renderControlRule = () => {
    const controlDimensionType = this.props.form.getFieldValue('controlDimensionType')
      ? this.props.form.getFieldValue('controlDimensionType')
      : '';
    const controlDimensionCondition = this.props.form.getFieldValue('controlDimensionCondition')
      ? this.props.form.getFieldValue('controlDimensionCondition')
      : '';
    const controlDimensionValue = this.props.form.getFieldValue('controlDimensionValue')
      ? this.props.form.getFieldValue('controlDimensionValue')
      : '';
    const controlStrategyCode = this.props.form.getFieldValue('controlStrategyCode')
      ? this.props.form.getFieldValue('controlStrategyCode')
      : '';
    if (
      controlDimensionType != '' &&
      controlDimensionCondition != '' &&
      controlDimensionValue != '' &&
      controlStrategyCode != ''
    ) {
      return (
        '满足匹配维度时,' +
        controlDimensionType +
        '需' +
        controlDimensionCondition +
        controlDimensionValue +
        ',否则' +
        controlStrategyCode
      );
    }
    return '满足匹配维度时,控制维度需满足条件,否则执行控制策略';
  };

  selectCompanyScope = e => {
    this.setState({ chooseCompanyScope: e.target.value });
  };

  selectCompany = values => {
    this.setState({
      relatedCompanies: values,
    });
  };

  getLabel(key, item) {
    let str = key.split('.');
    return `${item[str[0]]}${str.length > 1 ? '-' + item[str[1]] : ''}`;
  }

  getOptions = (item, type, index) => {
    let { formList2 } = this.state;
    !item.options &&
      item.method(item.params).then(res => {
        let data = item.listKey ? res.data[item.listKey] : res.data;
        formList2[index].fetching = true;
        formList2[index].options = data.map(option => {
          return {
            key: option[item.valueKey],
            label: this.getLabel(item.labelKey, option),
          };
        });
        this.setState({ formList2 });
      });
  };

  getRenderDynamic(array) {
    let fields = [];
    array.filter(item => item.fieldType !== 'LOCATION').map((item, index) => {
      //参与人同行人，添加级别，职务
      if (['PARTICIPANT', 'PARTICIPANTS'].includes(item.fieldType)) {
        item.valueKey = 'userOid'; //userOid
        item.style = {
          paddingLeft: 20,
        };
        fields.push({ ...item });
        let arr = [
          {
            id: item.fieldType === 'PARTICIPANT' ? '10001' : '20001',
            fieldType: 'chooser',
            name: `${item.name}职务`,
            linkedItem: true,
            valueKey: 'id',
            overide: {
              single: true,
              type: 'personDutyModel',
              labelKey: 'name',
              valueKey: 'value',
              onChange: this.setValue,
              listExtraParams: { systemCustomEnumerationType: '1002' },
            },
          },
          {
            id: item.fieldType === 'PARTICIPANT' ? '10002' : '20002',
            fieldType: 'chooser',
            name: `${item.name}级别`,
            valueKey: 'id',
            linkedItem: true,
            overide: {
              single: true,
              type: 'personRankModel',
              labelKey: 'name',
              valueKey: 'value',
              listExtraParams: { systemCustomEnumerationType: '1008' },
            },
          },
          {
            id: item.fieldType === 'PARTICIPANT' ? '10003' : '20003',
            fieldType: 'chooser',
            name: `${item.name}部门`,
            valueKey: 'departmentOid',
            linkedItem: true,
            style: {
              padding: '0px 20px',
            },
            overide: {
              single: true,
              type: 'department',
              labelKey: 'name',
              valueKey: 'departmentOid',
            },
          },
        ];
        arr.map(item => {
          fields.push({ ...item });
        });
      } else if (item.fieldType === 'GPS') {
        //item.valueKey='id' //userOid
        item.style = {
          paddingLeft: 20,
        };
        fields.push({ ...item });
        fields.push({
          id: '30001',
          linkedItem: true,
          valueKey: 'id',
          style: {
            padding: '0px 20px',
          },
          fieldType: 'CUSTOM_ENUMERATION',
          name: `${item.name}级别`,
        });
      } else {
        fields.push({ ...item });
      }
    });
    return fields;
  }

  handleEvent = (key, value) => {
    let { formList1, expenseTypeInfo } = this.state;
    switch (key) {
      case 'expenseType':
        {
          let fields = [];
          /* value.fields.filter(item=>item.fieldType!=='LOCATION').map((item,index)=>{
            //参与人同行人，添加级别，职务
            if(["PARTICIPANT",'PARTICIPANTS'].includes(item.fieldType)){
              item.valueKey='userOid' //userOid
              item.style={
                paddingLeft: 20
              }
              fields.push({...item})
              let arr = [
                {
                  id: item.fieldType ==='PARTICIPANT' ? '10001' : '20001',
                  fieldType: 'chooser',
                  name: `${item.name}职务`,
                  linkedItem: true,
                  valueKey: 'id',
                  overide: {
                    single: true,
                    type: 'personDutyModel',
                    labelKey: 'name',
                    valueKey: 'value',
                    onChange: this.setValue,
                    listExtraParams:{ systemCustomEnumerationType: '1002' }
                  }
                },
                {
                  id: item.fieldType ==='PARTICIPANT' ? '10002' : '20002',
                  fieldType: 'chooser',
                  name: `${item.name}级别`,
                  valueKey: 'id',
                  linkedItem: true,
                  overide:{
                    single: true,
                    type: 'personRankModel',
                    labelKey: 'name',
                    valueKey: 'value',
                    listExtraParams:{ systemCustomEnumerationType: '1008' }
                  }
                },
                {
                  id:item.fieldType ==='PARTICIPANT' ? '10003' : '20003',
                  fieldType: 'chooser',
                  name: `${item.name}部门`,
                  valueKey: 'departmentOid',
                  linkedItem: true,
                  style: {
                    padding:"0px 20px"
                  },
                  overide:{
                    single: true,
                    type: 'department',
                    labelKey: 'name',
                    valueKey: 'departmentOid',
                  }
                },
              ]
              arr.map(item=>{
                fields.push({...item})
              })
            }else if(item.fieldType==='GPS'){
              //item.valueKey='id' //userOid
              item.style={
                paddingLeft: 20
              }
              fields.push({...item})
              fields.push({
                id: '30001',
                linkedItem: true,
                valueKey: 'id',
                style: {
                  padding:"0px 20px"
                },
                fieldType: 'CUSTOM_ENUMERATION',
                name: `${item.name}级别`,
              })
            }else {
              fields.push({...item})
            }
          })*/
          fields = this.getRenderDynamic(value.fields);
          expenseTypeInfo = {
            ...value,
            fields,
          };
          this.getDynamicControlDimensionOptions(fields);
        }
        break;
    }
    this.setState({
      formList1,
      expenseTypeInfo,
    });
  };

  //获取地点
  handlePlace = () => {};

  renderFormItem(item, type, index) {
    switch (item.type || item.fieldType) {
      case 'CUSTOM_ENUMERATION':
        return (
          <Select>
            {item.options &&
              item.options.map(o => {
                return <Select.Option key={o.value}>{o.label}</Select.Option>;
              })}
          </Select>
        );
      case 'input': {
        return (
          <Input
            placeholder={item.placeholder || this.$t('common.please.enter')}
            onChange={e => this.handleEvent(item.id, e)}
            disabled={item.disabled}
            autoComplete="off"
          />
        );
      }
      //输入金额组件组件
      case 'LONG':
      case 'inputNumber': {
        return (
          <InputNumber
            style={{ width: '100%' }}
            precision={0}
            step={1}
            placeholder={item.placeholder || this.$t('common.please.enter')}
            onChange={e => this.handleEvent(item.id, e)}
            disabled={item.disabled}
          />
        );
      }
      //选择组件
      case 'select': {
        return (
          <Select
            allowClear
            placeholder={item.placeholder || this.$t('common.please.select')}
            onChange={e => this.handleEvent(item.id, e)}
            onFocus={() => this.getOptions(item, type, index)}
            disabled={item.disabled}
            labelInValue={!!item.entity}
            notFoundContent={
              !item.fetching ? <Spin size="small" /> : this.$t('agency.setting.no.result')
            }
            getPopupContainer={() => document.querySelector('.ant-layout-content > .ant-tabs')}
          >
            {item.options && item.options.map(item => <Option key={item.key}>{item.label}</Option>)}
          </Select>
        );
      }
      case 'chooser': {
        return (
          <Chooser
            placeholder={item.placeholder || this.$t('common.please.select')}
            disabled={item.disabled}
            type={item.listType}
            listTitle={item.listTitle}
            showClear={item.clear}
            onChange={e => this.handleEvent(item.id, e)}
            labelKey={item.labelKey}
            valueKey={item.valueKey}
            listExtraParams={item.listExtraParams}
            selectorItem={item.selectorItem}
            single={item.single}
          />
        );
      }
      case 'expense':
        return (
          <SelectApplicationType
            url={`${
              config.expenseUrl
            }/api/expense/application/type/query/expense/type/by/setOfBooksId`}
            onChange={e => this.handleEvent(item.id, e)}
            disabled={item.disabled}
            params={item.params}
          />
        );
      case 'selector':
        return (
          <Selector
            placeholder={this.$t('common.please.select')}
            selectorItem={item.selectorItem}
          />
        );
      case 'START_DATE_AND_END_DATE':
        return <DatePicker />;
    }
  }

  renderFields = (fileds, type) => {
    const { getFieldDecorator } = this.props.form;
    const { record } = this.state;
    let flag = type === 'expenseTypeInfo';
    const detail = this.props.match.params.detail === 'expense-policy-detail';
    const formItemLayout = {};
    let row = [];
    let itemObj = {};
    let col = fileds.map((item, index) => {
      itemObj[item.id] = item;
      let overide = item.overide || {};
      if (flag) {
        if (item.fieldType === 'START_DATE_AND_END_DATE') {
          overide = {
            style: {
              width: '125%',
            },
          };
        }
        if (['PARTICIPANTS', 'PARTICIPANT'].includes(item.fieldType)) {
          overide = {
            labelKey: 'fullName',
            valueKey: 'userOid',
            listExtraParams: {
              companyId: this.props.company.id,
            },
          };
        }
        if (item.fieldType === 'LOCATION') {
          //地点类型
          let options = [];
          overide = {
            onChange: this.handlePlace,
          };
        }
      }
      let rules = [
        {
          required: flag ? false : item.required,
          message: this.$t('common.can.not.be.empty', { name: item.label }), //name 不可为空
        },
      ];
      if (item.validate && item.id === 'expenseType') {
        rules.push({ validator: (item, value, callback) => this.validate(item, value, callback) });
      } else if (item.validator) {
        rules.push({ validator: item.validator });
      }
      let defaultValue = record.id ? record[item.id] || '' : item.defaultValue;
      if (flag && record.dynamicFields) {
        defaultValue = (record.dynamicFields.find(i => i.fieldId === item.id) || {}).defaultValue;
        console.log(defaultValue);
      }
      return (
        <Col
          key={item.id}
          className={this.props.match.params.detail}
          span={item.linkedItem ? 4 : flag ? 5 : 6}
          style={
            flag
              ? item.style
                ? item.style
                : { padding: item.linkedItem ? '0px 0px 0px 20px' : '0 40px' }
              : {}
          }
        >
          <FormItem {...formItemLayout} label={item.label || item.name}>
            {getFieldDecorator(item.id, {
              valuePropName: item.type === 'switch' ? 'checked' : 'value',
              initialValue: defaultValue,
              rules: rules,
            })(
              detail ? (
                <span>{flag ? defaultValue : record.id && record[item.id]}</span>
              ) : flag ? (
                renderFormItem({ type: item.fieldType, options: item.options }, overide)
              ) : (
                this.renderFormItem(item, type, index)
              )
            )}
          </FormItem>
        </Col>
      );
    });
    if (flag) {
      let baseItem = [];
      let linkItem = [];
      let rowItem = [];
      col.map(item => {
        if (
          ['PARTICIPANTS', 'PARTICIPANT', 'chooser', 'GPS'].includes(itemObj[item.key].fieldType) ||
          itemObj[item.key].linkedItem
        ) {
          linkItem.push(item);
        } else {
          baseItem.push(item);
        }
        //console.log(itemObj[item.key])
      });
      switch (linkItem.length) {
        case 4:
          {
            let temp = linkItem.slice(0, 4);
            rowItem.push(this.getRowItem(baseItem, temp));
          }
          break;
        case 8:
          {
            let temp1 = linkItem.slice(0, 4);
            let temp2 = linkItem.slice(4, 8);
            rowItem.push(this.getRowItem(baseItem, temp1));
            rowItem.push(this.getRowItem(baseItem, temp2));
          }
          break;
        case 10:
          {
            let temp1 = linkItem.slice(0, 4);
            let temp2 = linkItem.slice(4, 8);
            let temp3 = linkItem.slice(8, 10);
            rowItem.push(this.getRowItem(baseItem, temp1));
            rowItem.push(this.getRowItem(baseItem, temp2));
            temp3 = this.getRowItem(baseItem, temp3);
            temp3 = this.getRowItem(baseItem, temp3);
            rowItem.push(this.getRowItem(baseItem, temp3));
          }
          break;
      }

      if (baseItem.length) {
        for (let i = 0, j = 4; i < col.length; i = i + 4, j = i + 4) {
          rowItem.push(col.slice(i, j));
        }
      }
      //return row.map((item,index)=> <Row key={new Date().getTime()+index}>{item}</Row>);
      return rowItem.map((item, index) => <Row key={new Date().getTime() + index}>{item}</Row>);
    } else return col;
  };

  getRowItem(origin, target) {
    if (origin[0]) {
      target.push(origin[0]);
      origin.splice(0, 1);
    }
    return target;
  }

  //数组去重
  myConcat = (origin, key = 'id') => {
    let temp = {};
    let target = [];
    origin.map(item => (temp[item[key]] = item));
    for (let item in temp) {
      target.push(temp[item]);
    }
    return target;
  };

  handleDisabledEndDate = endValue => {
    if (!this.state.startValue || !endValue) {
      return false;
    }
    return endValue.valueOf() <= this.state.startValue.valueOf();
  };

  handleDisabledStartDate = startValue => {
    if (!this.state.endValue || !startValue) {
      return false;
    }
    return startValue.valueOf() > this.state.endValue.valueOf();
  };

  onDateChange = (field, value) => {
    this.setState({
      [field]: value,
    });
  };

  handleCopy = () => {
    const { id, setOfBooksId, typeFlag } = this.props.match.params;
    this.props.dispatch(
      routerRedux.push({
        pathname: `/admin-setting/expense-policy/new-expense-policy/${id}/${setOfBooksId}/${typeFlag}`,
      })
    );
  };

  getCompany = () => {
    const { record } = this.state;
    let str = '';
    str = record.allCompanyFlag
      ? '全部公司'
      : record.id &&
        record.relatedCompanies.map((item, index) => {
          str +=
            index === record.relatedCompanies.length - 1
              ? item.companyName
              : +item.companyName + '、';
          return str;
        });
    return str;
  };

  render() {
    const { getFieldDecorator } = this.props.form;
    const formItemLayout = {};
    const { enabled } = this.props.match.params;
    const {
      loading,
      record,
      formList1,
      formList2,
      controlStrategyOptions,
      dynamicControlDimensionOptions,
      messageOptions,
      expenseTypeInfo,
      controlDimension,
      controlDimensionConditions,
    } = this.state;
    const detail = this.props.match.params.detail === 'expense-policy-detail';
    return (
      <div className="new-expense-policy" style={{ paddingBottom: 70 }}>
        {detail && (
          <Row gutter={24} style={{ fontSize: 15, margin: '10px 0 10px -12px ' }}>
            <span style={{ margin: '0 25px 0 10px' }}>费用报销政策基本信息</span>
            <span>
              <a onClick={this.handleCopy}>复制</a>
            </span>
          </Row>
        )}
        <Form onSubmit={this.handleSave} onChange={this.handleChange}>
          <div style={{ marginBottom: detail ? 15 : 0, borderBottom: '1px solid #D0D0D0' }}>
            {this.$t('matching.dimension')}
          </div>
          <Row gutter={24} style={{ margin: '0 40px' }}>
            {this.renderFields(formList1, 'formList1')}
          </Row>
          {expenseTypeInfo.fields && expenseTypeInfo.fields.length ? (
            <Row
              style={{
                padding: detail ? '15px 15px 0' : '',
                margin: '0 52px 15px',
                border: '1px solid rgb(208,208,208)',
              }}
            >
              {this.renderFields(expenseTypeInfo.fields, 'expenseTypeInfo')}
            </Row>
          ) : null}
          <Row gutter={24} style={{ margin: '0 40px' }}>
            {this.renderFields(formList2, 'formList2')}
          </Row>
          <div style={{ marginBottom: detail ? 15 : 0, borderBottom: '1px solid #D0D0D0' }}>
            {this.$t('control.dimension')}
          </div>
          <Row gutter={24} style={{ margin: '0 40px' }}>
            <Col span={6} className={this.props.match.params.detail}>
              {/*政策控制策略*/}
              <FormItem label={this.$t('expense.policy.controlStrategyName')} colon={true}>
                {getFieldDecorator('controlStrategyCode', {
                  initialValue: record.controlStrategyCode,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  detail ? (
                    <span>{record.controlStrategyName}</span>
                  ) : (
                    <Select
                      allowClear
                      labelInValue
                      placeholder={this.$t({ id: 'common.please.select' }) /*请选择*/}
                    >
                      {controlStrategyOptions.map(option => {
                        return <Option key={option.value}>{option.messageKey}</Option>;
                      })}
                    </Select>
                  )
                )}
              </FormItem>
            </Col>
            <Col span={6} className={this.props.match.params.detail}>
              {/*消息*/}
              <FormItem label={this.$t('expense.policy.messageName')} colon={true}>
                {getFieldDecorator('messageCode', {
                  initialValue: record.messageCode,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  detail ? (
                    <Popover
                      className="detail-item"
                      content={
                        record.id &&
                        record.messageCode &&
                        messageOptions.find(item => record.messageCode === item.value).name
                      }
                    >
                      {record.id &&
                        record.messageCode &&
                        messageOptions.find(item => record.messageCode === item.value).name}
                    </Popover>
                  ) : (
                    <Select
                      allowClear
                      placeholder={this.$t({ id: 'common.please.select' }) /*请选择*/}
                    >
                      {messageOptions.map(option => {
                        return <Option key={option.value}>{option.messageKey}</Option>;
                      })}
                    </Select>
                  )
                )}
              </FormItem>
            </Col>
            {detail ? (
              <Col span={10} style={{ margin: 10 }} className={this.props.match.params.detail}>
                <span style={{ marginRight: 8, color: 'rgba(0, 0, 0, 0.85)' }}>控制维度:</span>
                {record.options}
              </Col>
            ) : (
              <span>
                <Col span={4} className={this.props.match.params.detail}>
                  {/*控制维度*/}
                  <FormItem label={this.$t('expense.policy.controlDimensionTypeName')} colon={true}>
                    {getFieldDecorator('controlDimensionType', {
                      initialValue: record.controlDimensionType,
                      rules: [
                        {
                          required: true,
                          message: this.$t('common.please.enter'),
                        },
                      ],
                    })(
                      <Select
                        allowClear
                        onChange={this.selectControlDimension}
                        placeholder={this.$t({ id: 'common.please.select' }) /*请选择*/}
                      >
                        {dynamicControlDimensionOptions.map(option => {
                          return <Option key={option.value}>{option.name}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={3} className={this.props.match.params.detail}>
                  {/*控制条件*/}
                  <FormItem
                    label={this.$t('expense.policy.controlDimensionCondition')}
                    colon={true}
                  >
                    {getFieldDecorator('judgementSymbol', {
                      initialValue: record.judgementSymbol,
                      rules: [
                        {
                          required: true,
                          message: this.$t('common.please.enter'),
                        },
                      ],
                    })(
                      <Select allowClear>
                        {controlDimensionConditions.map(option => {
                          return <Option key={option.value}>{option.messageKey}</Option>;
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
                <Col span={4} className={this.props.match.params.detail}>
                  {/*值*/}
                  {controlDimension ? (
                    <FormItem label={this.$t('expense.policy.controlDimensionValue')} colon={true}>
                      {getFieldDecorator('controlDimensionValue', {
                        initialValue: record.id
                          ? record.controlDimensions.length && record.controlDimensions[0].value
                          : '', //controlDimension.value,
                        rules: [{ required: true, message: this.$t('common.please.enter') }],
                      })(
                        controlDimension.id ? (
                          renderFormItem(controlDimension, controlDimension.overide)
                        ) : (
                          <InputNumber />
                        )
                      )}
                    </FormItem>
                  ) : (
                    ''
                  )}
                </Col>
              </span>
            )}
          </Row>
          <Row gutter={24} style={{ margin: '0 40px' }}>
            <Col span={6} className={this.props.match.params.detail}>
              {/*有效日期从*/}
              <FormItem label={this.$t('expense.policy.startDate')} colon={true}>
                {getFieldDecorator('startDate', {
                  initialValue: record.id ? moment(record.startDate) : null,
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.please.enter'),
                    },
                  ],
                })(
                  detail ? (
                    <span className="detail-item">
                      {record.startDate && moment(record.startDate).format('YYYY-MM-DD')}
                    </span>
                  ) : (
                    <DatePicker
                      allowClear
                      onChange={e => this.onDateChange('startValue', e)}
                      disabledDate={this.handleDisabledStartDate}
                      style={{ width: '100%' }}
                    />
                  )
                )}
              </FormItem>
            </Col>

            <Col span={6} className={this.props.match.params.detail}>
              {/*有效日期至*/}
              <FormItem label={this.$t('expense.policy.endDate')} colon={true}>
                {getFieldDecorator('endDate', {
                  initialValue: record.endDate ? moment(record.endDate) : null,
                })(
                  detail ? (
                    <span className="detail-item">
                      {record.endDate ? moment(record.endDate).format('YYYY-MM-DD') : '-'}
                    </span>
                  ) : (
                    <DatePicker
                      allowClear
                      onChange={e => this.onDateChange('endValue', e)}
                      disabledDate={this.handleDisabledEndDate}
                      style={{ width: '100%' }}
                    />
                  )
                )}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24} style={{ margin: '0 40px' }}>
            <Col span={8} className="expense-policy-control">
              <FormItem label="控制规则" colon={true}>
                {this.renderControlRule()}
              </FormItem>
            </Col>
          </Row>
          <Row gutter={24} style={{ margin: '0px 51px' }}>
            <Col span={24} className="expense-policy-adjust-company" style={{ paddingLeft: 0 }}>
              <FormItem label="适用公司" colon={true}>
                {detail ? (
                  this.getCompany()
                ) : (
                  <div>
                    <RadioGroup
                      onChange={this.selectCompanyScope}
                      value={this.state.chooseCompanyScope}
                    >
                      <Radio value={1}>全部公司</Radio>
                      <Radio value={2}>部分公司</Radio>
                    </RadioGroup>
                    {this.state.chooseCompanyScope == 2 && (
                      <div style={{ width: 200 }}>
                        <Chooser
                          placeholder={this.$t({ id: 'common.please.select' })}
                          value={this.state.relatedCompanies}
                          type={'company'}
                          single={false}
                          labelKey="name"
                          valueKey="id"
                          showNumber
                          listExtraParams={{ setOfBooksId: this.props.match.params.setOfBooksId }}
                          onChange={this.selectCompany}
                        />
                      </div>
                    )}
                  </div>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row
            gutter={20}
            style={{ margin: `${this.state.chooseCompanyScope === 2 ? '15px' : '0px'} 51px` }}
          >
            <FormItem
              style={{ width: '8%', paddingLeft: 0 }}
              className="expense-enabled-jsq"
              label={this.$t('expense.policy.enabled') /*启用*/}
            >
              {getFieldDecorator('enabled', {
                valuePropName: 'checked',
                initialValue: record.id ? record.enabled : enabled,
              })(<Switch disabled={detail} />)}
              <span style={{ marginLeft: 8 }}>
                {!!this.props.form.getFieldValue('enabled') ? '启用' : '禁用'}
              </span>
            </FormItem>
          </Row>
          {detail ? (
            <Affix
              className="bottom-bar-approve"
              style={{
                height: '50px',
                boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                background: '#fff',
                lineHeight: '50px',
                zIndex: 1,
                paddingLeft: 20,
              }}
            >
              <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleCancel}>
                <Icon type="rollback" style={{ marginRight: '5px' }} />
                {this.$t({ id: 'common.back' })}
              </a>
            </Affix>
          ) : (
            <div style={{ margin: '35px 50px' }}>
              <Button onClick={this.handleSave} type="primary" loading={loading} htmlType="submit">
                {this.$t({ id: 'common.save' }) /*保存*/}
              </Button>
              <Button onClick={this.handleCancel} style={{ marginLeft: 10 }}>
                {this.$t({ id: 'common.cancel' }) /*取消*/}
              </Button>
            </div>
          )}
        </Form>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

//本组件需要用一下Form表单属性
const WrappedNewExpensePolicy = Form.create()(NewExpensePolicy);
export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedNewExpensePolicy);
