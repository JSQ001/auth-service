import React from 'react';
import { Form, Input, Select, message, Row, Col, Button, Checkbox, Icon, DatePicker } from 'antd';
import Lov from 'widget/Template/lov';
import { messages } from 'utils/utils';
import Chooser from 'widget/chooser';
import { connect } from 'dva';
import styles from './components.scss';

const FormItem = Form.Item;
const { Option } = Select;

class FundSearchForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false, // 是否展开
      intervalFrom: '',
      intervalTo: '',
      accountNumberObj: '',
      startDate: '',
      endDate: '',
    };
  }

  componentWillMount() {
    const { searchForm, itemLayout } = this.props;
    if (itemLayout === undefined) {
      const formItemLayout = {};
      this.setState({
        formItemLayout,
      });
    } else {
      const formItemLayout = itemLayout;
      this.setState({
        formItemLayout,
      });
    }

    this.setState({
      searchForm,
    });
  }

  getLastKey = key => {
    return key.split('.')[key.split('.').length - 1];
  };

  /**
   * 收起和展开
   */
  toggle = () => {
    const { expand } = this.state;
    this.setState({ expand: !expand });
  };

  /**
   * 搜索按钮
   */
  handleSearch = e => {
    if (e) e.preventDefault();
    const { intervalFrom, intervalTo } = this.state;
    const {
      form: { validateFields },
      searchForm,
      submitHandle,
    } = this.props;
    validateFields((err, values) => {
      if (!err) {
        const intervalArr = searchForm.filter(item => {
          const { type } = item;
          return type === 'intervalInput';
        });
        if (intervalArr.length > 0) {
          // eslint-disable-next-line
          values[intervalArr[0].id] = {
            intervalFrom,
            intervalTo,
          };
        }
        for (const propName in values) {
          // eslint-disable-next-line
          if (!values[propName]) delete values[propName];
        }
        submitHandle(values);
      }
    });
  };

  /**
   * 清除搜索
   */
  handleClear = () => {
    const {
      form: { resetFields },
    } = this.props;
    this.setState({
      intervalFrom: '',
      intervalTo: '',
      accountNumberObj: '',
    });
    resetFields();
  };

  /**
   * 获取值列表并设置
   */
  getValueList = async item => {
    let options = {};
    if (item.customizeOptions && item.customizeOptions.length > 0) {
      options = item.customizeOptions;
    } else {
      await this.getSystemValueList(item.valueListCode)
        .then(res => {
          if (res.data.values.length > 0) {
            options = res.data.values;
          }
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
    let { searchForm } = this.state;
    searchForm = searchForm.map(searchItem => {
      if (searchItem.id === item.id) {
        Object.defineProperty(searchItem, 'options', {
          value: options,
        });
      }
      return searchItem;
    });
    this.setState({ searchForm });
  };

  /**
   * 渲染Form
   */
  renderForm = () => {
    const {
      form: { getFieldDecorator },
      maxLength,
      user,
    } = this.props;
    const { searchForm, expand, accountNumberObj, startDate, endDate } = this.state;
    const count = expand ? searchForm.length : maxLength;
    const { formItemLayout } = this.state;
    const children = [];
    searchForm.forEach((item, i) => {
      let itemInitialValue = '';
      if (item.listType === 'paymentAccount') {
        itemInitialValue = accountNumberObj.accountNumber;
      }
      if (item.isNeedUserInitialValue) {
        itemInitialValue = [
          {
            userId: user.id,
            userName: user.userName,
          },
        ];
      }
      if (item.type === 'intervalDate') {
        children.push(
          <Col
            className={styles.customForm}
            span={item.colSpan || 24}
            style={{
              display: maxLength === undefined || i < count ? 'block' : 'none',
              paddingLeft: '20px',
              paddingRight: '20px',
              color: '#666666',
            }}
            key={item.id}
          >
            <Row>
              <Col span={11}>
                <FormItem label={item.fromlabel} colon={false}>
                  {getFieldDecorator(item.fromId)(
                    <DatePicker
                      onChange={date => {
                        this.setState({
                          startDate: date,
                        });
                        if (endDate) {
                          if (new Date(date) > new Date(endDate)) {
                            message.error('起始日期大于结束日期');
                          }
                        }
                      }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={11} offset={2}>
                <FormItem label={item.tolabel} colon={false}>
                  {getFieldDecorator(item.toId)(
                    <DatePicker
                      onChange={date => {
                        this.setState({
                          endDate: date,
                        });
                        if (startDate && date) {
                          if (new Date(date) < new Date(startDate)) {
                            message.error('结束日期小于结束日期');
                          }
                        }
                      }}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
          </Col>
        );
      } else {
        children.push(
          <Col
            className={styles.customForm}
            span={item.colSpan || 24}
            style={{
              display: maxLength === undefined || i < count ? 'block' : 'none',
              paddingLeft: '20px',
              paddingRight: '20px',
              color: '#666666',
            }}
            key={item.id}
          >
            <FormItem label={item.label} {...formItemLayout} colon={false}>
              {getFieldDecorator(item.id, {
                rules: [
                  {
                    required: item.isRequired,
                    message: messages('common.can.not.be.empty', { name: item.label }),
                  },
                ],
                initialValue: itemInitialValue,
              })(this.renderFormItem(item))}
            </FormItem>
          </Col>
        );
      }
    });
    return children;
  };

  /**
   *  渲染FormItem里面的内容
   */
  renderFormItem = item => {
    const { intervalFrom, intervalTo } = this.state;
    switch (item.type) {
      // 普通输入框
      case 'input': {
        return (
          <Input
            allowClear
            placeholder={item.placeholder || messages('common.please.enter')}
            disabled={item.disabled}
            onPressEnter={this.handleSearch}
          />
        );
      }
      // 弹出列表选择组件
      case 'modalList': {
        if (item.listType === 'paymentAccount') {
          return (
            // <Input
            //   allowClear
            //   placeholder={item.placeholder || messages('common.please.select')}
            //   disabled={item.disabled}
            //   setfieldsvalue={accountNumberObj.accountNumber}
            //   onClick={() => {
            //     this.getBackList();
            //     this.setState({
            //       visible: true,
            //     });
            //   }}
            // />
            <Lov
              code="bankaccount_choose"
              valueKey="id"
              labelKey="accountNumber"
              single
              onChange={this.onChange}
            />
          );
        } else {
          return (
            <Chooser
              placeholder={item.placeholder || messages('common.please.select')}
              disabled={item.disabled}
              type={item.listType}
              listTitle={item.listTitle}
              showClear={item.clear}
              labelKey={this.getLastKey(item.labelKey)}
              valueKey={this.getLastKey(item.valueKey)}
              listExtraParams={item.listExtraParams}
              selectorItem={item.selectorItem}
              single={item.single}
            />
          );
        }
      }
      // 区间型输入
      case 'intervalInput': {
        return (
          <Row>
            <Col span={10}>
              <Input
                allowClear
                placeholder={item.placeholder || messages('common.please.enter')}
                disabled={item.disabled}
                onPressEnter={this.handleSearch}
                value={intervalFrom}
                onChange={this.changeIntervalFrom}
              />
            </Col>
            <Col span={4} style={{ textAlign: 'center' }}>
              <span style={{ lineHeight: 'normal' }}>至</span>
            </Col>
            <Col span={10}>
              <Input
                allowClear
                placeholder={item.placeholder || messages('common.please.enter')}
                disabled={item.disabled}
                value={intervalTo}
                onPressEnter={this.handleSearch}
                onChange={this.changeIntervalTo}
              />
            </Col>
          </Row>
        );
      }

      case 'checkBox': {
        return (
          <Col style={{ height: '32px' }}>
            <Checkbox />
          </Col>
        );
      }

      // 值列表选择组件
      case 'valueList': {
        return (
          <Select
            labelInValue
            allowClear
            placeholder="请选择"
            onFocus={() => this.getValueList(item)}
          >
            {item.options &&
              item.options.map(option => {
                return <Option key={option.value}>{option.name}</Option>;
              })}
          </Select>
        );
      }

      // 日期选择
      case 'datePicker': {
        return <DatePicker />;
      }

      // 启用状态
      case 'enabledStateSelect': {
        return (
          <Select labelInValue allowClear placeholder="请选择">
            {item.options &&
              item.options.map(option => {
                return <Option key={option.value}>{option.name}</Option>;
              })}
          </Select>
        );
      }

      // 默认为输入框
      default: {
        return (
          <Input
            allowClear
            placeholder={item.placeholder || messages('common.please.enter')}
            disabled={item.disabled}
            onPressEnter={this.handleSearch}
          />
        );
      }
    }
  };

  /**
   * 区间从输入设置值
   */
  changeIntervalFrom = event => {
    this.setState({
      intervalFrom: event.target.value,
    });
    const { intervalTo } = this.state;
    if (intervalTo !== '' && Number(intervalTo) < Number(event.target.value)) {
      message.error(`区间从大于了区间至！`);
    }
  };

  /**
   * 区间至输入设置值
   */
  changeIntervalTo = event => {
    this.setState({
      intervalTo: event.target.value,
    });
    const { intervalFrom } = this.state;
    if (event.target.value !== '' && Number(intervalFrom) > Number(event.target.value)) {
      message.error(`区间至小于了区间从！`);
    }
  };

  render() {
    const { expand } = this.state;
    const { maxLength } = this.props;
    return (
      <div>
        <Form>
          {this.renderForm()}
          <Col span={24} style={{ textAlign: 'right' }}>
            {maxLength === undefined ? null : (
              <a
                style={{ display: 'inline-block', marginRight: '20px' }}
                className="toggle-button"
                onClick={this.toggle}
              >
                {expand ? messages('common.fold') : messages('common.more')}
                <Icon type={expand ? 'up' : 'down'} />
              </a>
            )}
            <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
              搜索
            </Button>
            <Button style={{ marginLeft: 8 }} onClick={this.handleClear}>
              清空
            </Button>
          </Col>
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

export default connect(map)(Form.create()(FundSearchForm));

/**
 * searchForm: [
 *  //值列表两种情况
 *  1.从后台获取
        {
          colSpan: 6,
          type: 'valueList',
          label: '系统来源',
          id: 'sourceSystem',
          options: [],
          valueListCode: 'ZJ_SOURCE',
        },
    2.从前台自定义
        {
          colSpan: 6,
          type: 'valueList',
          label: '测试',
          id: 'test',
          options: [],
          customizeOptions: [
            { value: 1, name: '测试1' }, { value: 2, name: '测试2' }
          ],
          valueListCode: '',
        },
    // modal弹框
        {
          colSpan: 6,
          type: 'modalList',
          label: '单据公司',
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
    // 普通输入框
        {
          colSpan: 6,
          type: 'input',
          label: '收款账户',
          id: 'gatherAccountName',
        },
    // 区间输入
        {
          colSpan: 6,
          type: 'intervalInput',
          label: '信用分区间',
          id: 'creditScore',
        },
    // checbox
        {
          colSpan: 6,
          type: 'checkBox',
          label: '包含子公司',
          id: 'childCompanyFlag',
        },
    // 日期区间
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '日期从',
          fromId: 'dateFrom',
          tolabel: '日期到',
          toId: 'dateTo',
        },
    // 银行账号
        {
          colSpan: 6,
          type: 'modalList',
          label: '银行账号',
          id: 'paymentAccount',
          listType: 'paymentAccount',
        },
      ],
 */
