import React from 'react';
import { Select, Radio } from 'antd';
import ListSelector from 'widget/new-list-selector';
import { SelectDepOrPerson } from 'widget/index';
import PropTypes from 'prop-types';
import { connect } from 'dva';

const RadioGroup = Radio.Group;

/**
 * 权限分配组件 全部人员，人员，部门，公司
 */
class PermissionsAllocation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: [],
      type: '',
      showSelectDepartment: false,
      listVisible: false,
      listSelectorType: undefined,
      selectedList: [],
      selectEmployeeText: '',
      extraParams: undefined,
    };
  }

  componentDidMount() {
    const { value: model } = this.props;
    if (model) {
      const result = model.values ? model.values.map(item => ({ ...item, id: item.key })) : [];
      this.setState({ type: model.type, selectedList: result, value: result }, () => {
        this.setSelectEmployeeText();
      });
    }
  }

  componentWillReceiveProps(nextProps) {
    const model = nextProps.value;
    if (model) {
      const result = model.values ? model.values.map(item => ({ ...item, id: item.key })) : [];
      this.setState({ type: model.type, selectedList: result, value: result }, () => {
        this.setSelectEmployeeText();
      });
    }
  }

  // 选择切换
  radioChange = e => {
    this.setState({ type: e.target.value, selectedList: [] }, () => {
      this.setSelectEmployeeText();
      this.onChange();
    });
  };

  // 切换文字
  setSelectEmployeeText = () => {
    const { type, selectedList } = this.state;
    let text = '';
    let listSelectorType;
    if (type === 'department') {
      text = this.$t('common.selected.number.department', { number: selectedList.length });
    } else if (type === 'employee') {
      text = this.$t('common.selected.number.user', { number: selectedList.length });
      listSelectorType = 'select_setOfBooksId_employee';
    } else if (type === 'company') {
      text = this.$t('common.selected.number.user.company', { number: selectedList.length });
      listSelectorType = 'company';
    } else if (type === 'all') {
      text = this.$t('common.all.user');
    }
    this.setState({ selectEmployeeText: text, listSelectorType });
  };

  // 切换弹框 type
  showSelectEmployee = () => {
    const { type } = this.state;
    const {
      company: { setOfBooksId },
    } = this.props;
    this.selectEmployee.blur();
    if (type === 'department') {
      this.setState({ showSelectDepartment: true, extraParams: undefined });
    } else if (type === 'employee') {
      this.setState({ listVisible: true, extraParams: undefined });
    } else if (type === 'company') {
      this.setState({ listVisible: true, extraParams: { setOfBooksId } });
    }
  };

  // 部门确认
  handSelectDept = values => {
    const value = values.map(item => {
      return {
        label: item.name,
        key: item.id,
        value: item.id,
      };
    });

    this.setState({ selectedList: value, value }, () => {
      this.setSelectEmployeeText();
      this.onChange(value);
      this.handleListCancel();
    });
  };

  // ListSelector 确认
  handleListSelectorOk = value => {
    const result = value.result.map(item => {
      return {
        label: item.name || item.fullName,
        key: item.id,
        value: item.id,
        id: item.id,
      };
    });
    this.setState({ value: result, selectedList: result }, () => {
      this.setSelectEmployeeText();
      this.onChange(result);
      this.handleListCancel();
    });
  };

  onChange = values => {
    const { onChange } = this.props;
    const { type } = this.state;
    if (onChange) {
      onChange({ type, values: values || [] });
    }
  };

  // 关闭弹框
  handleListCancel = () => {
    this.setState({
      showSelectDepartment: false,
      listVisible: false,
    });
  };

  render() {
    const {
      value,
      selectEmployeeText,
      type,
      showSelectDepartment,
      selectedList,
      listVisible,
      listSelectorType,
      selectorItem,
      extraParams,
    } = this.state;
    const { disabled, hiddenComponents } = this.props;

    const textStyle = {
      position: 'absolute',
      top: 3,
      left: 10,
      right: 10,
      width: 180,
      height: 26,
      lineHeight: '26px',
      background: type === 'all' || disabled ? '#f5f5f5' : '#fff',
      color: type === 'all' || disabled ? 'rgba(0, 0, 0, 0.25)' : 'rgba(0, 0, 0, 0.65)',
      cursor: 'pointer',
    };

    return (
      <div>
        <RadioGroup onChange={this.radioChange} value={type}>
          {hiddenComponents.indexOf('all') >= 0 || (
            <Radio disabled={disabled} value="all">
              {this.$t('common.all.user')}
            </Radio>
          )}
          {hiddenComponents.indexOf('employee') >= 0 || (
            <Radio disabled={disabled} value="employee">
              {this.$t('common.add.by.users') /* {按人员添加} */}
            </Radio>
          )}
          {hiddenComponents.indexOf('department') >= 0 || (
            <Radio disabled={disabled} value="department">
              {this.$t('common.add.by.department')}
            </Radio>
          )}
          {hiddenComponents.indexOf('company') >= 0 || (
            <Radio disabled={disabled} value="company">
              {this.$t('common.add.by.companys') /* {按公司添加} */}
            </Radio>
          )}
        </RadioGroup>
        {type && (
          <div
            style={{
              position: 'relative',
              width: '100%',
              height: 32,
              lineHeight: '32px',
              marginTop: 10,
            }}
          >
            <Select
              disabled={type === 'all' || disabled}
              value={value}
              ref={ref => {
                this.selectEmployee = ref;
              }}
              onFocus={this.showSelectEmployee}
              dropdownStyle={{ display: 'none' }}
              labelInValue
              style={{ minWidth: 200, maxWidth: 300 }}
            />
            <div
              style={textStyle}
              onClick={() => {
                if (!disabled) {
                  this.showSelectEmployee();
                }
              }}
            >
              {selectEmployeeText}
            </div>
          </div>
        )}

        <ListSelector
          visible={listVisible}
          onCancel={this.handleListCancel}
          type={listSelectorType}
          selectorItem={selectorItem}
          extraParams={extraParams}
          selectedData={selectedList}
          onOk={this.handleListSelectorOk}
          labelKey="name"
          valueKey="id"
          showDetail={false}
        />

        <SelectDepOrPerson
          visible={showSelectDepartment}
          onCancel={this.handleListCancel}
          onConfirm={this.handSelectDept}
          onOk={this.handleListOk}
          single
          onlyDep
          title={this.$t('chooser.data.dep.title') /* 选择部门 */}
          renderButton={false}
          noFooter
          selectedData={selectedList}
        />
      </div>
    );
  }
}

PermissionsAllocation.propTypes = {
  disabled: PropTypes.bool, // 是否可用  type值 all department employee company
  value: PropTypes.object, // 已选择的值 { type: 'all', values: [{ key: "", value: "", label: ""]}
  hiddenComponents: PropTypes.array, // 不需要显示的组件 ["all","department","employee", "company"] 表示这三个组件不需要显示 默认全部都显示
};

PermissionsAllocation.defaultProps = {
  hiddenComponents: [],
  disabled: false,
  value: undefined,
};

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(PermissionsAllocation);
