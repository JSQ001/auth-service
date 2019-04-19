import React from 'react';
import { Select, Radio } from 'antd';

const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;

import { SelectDepOrPerson } from 'widget/index';
import SelectEmployeeGroup from 'widget/Template/select-employee-group';
import PropTypes from 'prop-types';
import ListSelector from 'widget/new-list-selector';

/**
 * 权限分配组件(全部人员，按人员添加，按部门添加，按人员组添加)
 * (add by zhaozhu @20190327)
 */
class PermissionsAllocation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: [],
      type: '',
      showSelectDepartment: false,
      showSelectEmployeeGroup: false,
      showSelectEmployee: false,
      selectedList: [],
      selectEmployeeText: '',
      itemSelectorItem: {},
    };
  }

  componentWillMount() {
    let model = this.props.value;

    model &&
      this.setState({ type: model.type, selectedList: model.values, value: model.values }, () => {
        this.setSelectEmployeeText();
      });
  }

  componentWillReceiveProps(nextProps) {
    let model = nextProps.value;
    let selectedList = [];
    model.values.map(item => {
      let temp = { ...item };
      temp.contactId = item.key;
      temp.fullName = item.label;

      selectedList.push(temp);
    });
    model &&
      this.setState({ type: model.type, selectedList: selectedList, value: model.values }, () => {
        this.setSelectEmployeeText();
      });
  }

  onApplyEmployee = e => {
    this.setState({ type: e.target.value, selectedList: [] }, () => {
      this.setSelectEmployeeText();
      this.onChange();
    });
  };

  setSelectEmployeeText = () => {
    let text = '';
    if (this.state.type === 'department') {
      text = this.$t('common.selected.number.department', {
        number: this.state.selectedList.length,
      });
    } else if (this.state.type === 'group') {
      text = this.$t('common.selected.number.user.group', {
        number: this.state.selectedList.length,
      });
    } else if (this.state.type === 'all') {
      text = this.$t('common.all.type');
    } else if (this.state.type === 'user') {
      text = this.$t('common.selected.number.user', {
        number: this.state.selectedList.length,
      });
    }
    this.setState({ selectEmployeeText: text });
  };

  showSelectEmployeeGroup = () => {
    this.refs.selectEmployeeGroup.blur();
    if (this.state.type === 'department') {
      this.setState({ showSelectDepartment: true });
    } else if (this.state.type === 'group') {
      this.setState({ showSelectEmployeeGroup: true });
    } else if (this.state.type === 'user') {
      this.setState({ showSelectEmployee: true });
    }
  };

  handleListCancel = () => {
    this.setState({
      showSelectEmployee: false,
      showSelectEmployeeGroup: false,
      showSelectDepartment: false,
    });
  };

  handleListOk = values => {
    let value;
    if (values.type != 'dimUser') {
      value = values.checkedKeys.map(item => {
        return {
          label: item.label,
          key: item.value,
          value: item.value,
        };
      });
      this.setState({ selectedList: values.checkedKeys, value: value }, () => {
        this.setSelectEmployeeText();
        this.onChange(value);
        this.handleListCancel();
      });
    } else {
      value = values.result.map(item => {
        return {
          label: null,
          key: item.contactId,
          value: item.contactId,
        };
      });
      this.setState({ selectedList: values.result, value: value }, () => {
        this.setSelectEmployeeText();
        this.onChange(value);
        this.handleListCancel();
      });
    }
  };

  onChange = values => {
    const onChange = this.props.onChange;
    if (onChange) {
      onChange({ type: this.state.type, values: values || [] });
    }
  };

  handSelectDept = values => {
    let value = values.map(item => {
      return {
        label: item.name,
        key: item.id,
        value: item.id,
      };
    });

    this.setState({ selectedList: values, value: value }, () => {
      this.setSelectEmployeeText();
      this.onChange(value);
      this.handleListCancel();
    });
  };

  /**
   * 按人员弹窗保存事件
   */
  handleListSelectorOk = values => {
    let { data } = this.state;
    data[this.state.recordKey].solutionParameterList = values.result;
    this.setState({
      data: data,
      listSelectorVisible: false,
    });
  };

  render() {
    const {
      value,
      selectEmployeeText,
      type,
      showSelectEmployee,
      showSelectEmployeeGroup,
      showSelectDepartment,
      selectedList,
    } = this.state;
    const { disabled } = this.props;
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
        <RadioGroup onChange={this.onApplyEmployee} value={type}>
          {this.props.hiddenComponents.indexOf('all') >= 0 || (
            <Radio disabled={disabled} value="all">
              {this.$t('common.all.user')}
            </Radio>
          )}
          {this.props.hiddenComponents.indexOf('user') >= 0 || (
            <Radio disabled={disabled} value="user">
              {this.$t('common.add.by.users')}
            </Radio>
          )}
          {this.props.hiddenComponents.indexOf('department') >= 0 || (
            <Radio disabled={disabled} value="department">
              {this.$t('common.add.by.department')}
            </Radio>
          )}
          {this.props.hiddenComponents.indexOf('group') >= 0 || (
            <Radio disabled={disabled} value="group">
              {this.$t('common.add.by.user.group')}
            </Radio>
          )}
        </RadioGroup>
        {type &&
          type !== 'all' && (
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
                disabled={type === 'all' || this.props.disabled}
                value={value}
                ref="selectEmployeeGroup"
                onFocus={this.showSelectEmployeeGroup}
                dropdownStyle={{ display: 'none' }}
                labelInValue
                style={{ minWidth: 200, maxWidth: 300 }}
              />
              <div
                style={textStyle}
                onClick={() => {
                  !this.props.disabled && this.showSelectEmployeeGroup();
                }}
              >
                {selectEmployeeText}
              </div>
            </div>
          )}

        {showSelectEmployeeGroup && (
          <SelectEmployeeGroup
            visible={showSelectEmployeeGroup}
            onCancel={this.handleListCancel}
            onOk={this.handleListOk}
            single={true}
            selectedData={selectedList}
            mode={this.props.mode}
          />
        )}
        <SelectDepOrPerson
          visible={showSelectDepartment}
          onCancel={this.handleListCancel}
          onOk={this.handleListOk}
          single={true}
          onlyDep={true}
          title={'选择部门'}
          onConfirm={this.handSelectDept}
          renderButton={false}
          noFooter={true}
          selectedData={selectedList}
          mode={this.props.mode}
        />
        <ListSelector
          visible={showSelectEmployee}
          onCancel={this.handleListCancel}
          type={'dimUser'}
          //selectorItem={JSON.stringify(itemSelectorItem) === '{}' ? undefined : itemSelectorItem}
          extraParams={{ roleType: 'TENANT' }}
          selectedData={[...selectedList]}
          onOk={this.handleListOk}
          labelKey="fullName"
          valueKey="contactId"
        />
      </div>
    );
  }
}

PermissionsAllocation.propTypes = {
  onChange: PropTypes.func, //进行选择后的回调
  disabled: PropTypes.bool, //是否可用
  value: PropTypes.object, //已选择的值 {key: "",value: "",label:""}
  hiddenComponents: PropTypes.array, //不需要显示的组件 ["all","department","group"] 表示这三个组件不需要显示 默认全部都显示
  mode: PropTypes.string,
};

PermissionsAllocation.defaultProps = {
  hiddenComponents: [],
  disabled: false,
  mode: 'id',
};

export default PermissionsAllocation;
