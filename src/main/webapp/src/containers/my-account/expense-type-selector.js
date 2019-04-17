import React from 'react';
import PropTypes from 'prop-types';
import { Row, Col, Select, Input, Modal, Spin, Button, Card } from 'antd';
import httpFetch from 'share/httpFetch';
import config from 'config';
import 'styles/my-account/expense-type.selector.scss';
const Option = Select.Option;

class ExpenseTypeSelector extends React.Component {
  static getDerivedStateFromProps(nextProps) {
    // Should be a controlled component.
    if ('value' in nextProps) {
      return {
        ...(nextProps.value || {}),
      };
    }
    return null;
  }

  constructor(props) {
    super(props);
    this.state = {
      value: props.value || '',
      loading: true,
      visible: false,
      expenseType: [],
      selectedKeys: {},
      categoryType: [],
      selected: [],
      categoryLoading: false,
      searchParams: {},
      isShowHistoryExpenseType: true, //是否展示费用选择历史，搜索的时候不展示
      historyExpenseType: [], //费用选择历史记录
    };
    //this.handleSearch = debounce(this.handleSearch, 500);
  }

  componentDidMount() {
    this.getList();
  }

  getList() {
    const { setOfBooksId } = this.props.params;
    this.setState({ loading: true });
    httpFetch
      .get(`${config.expenseUrl}/api/expense/types/${setOfBooksId}/query`, this.state.params)
      .then(res => {
        res.map(item => {
          item.key = item.id;
          item.label = item.name;
        });
        this.setState({
          expenseType: res.data,
          loading: false,
        });
      });
  }

  handleFocus = () => {
    this.setState({ visible: true });
  };

  handleOk = e => {
    this.setState({ visible: false });
    this.props.onChange && this.props.onChange(this.state.selected);
  };

  handleCancel = e => {
    this.setState({
      searchParams: {},
      visible: false,
    });
  };

  handleSelect = expense => {
    let { selectedKeys } = this.state;
    selectedKeys = this.props.single ? {} : selectedKeys;
    if (selectedKeys[expense.id]) {
      delete selectedKeys[expense.id];
    } else {
      selectedKeys[expense.id] = expense;
    }
    let selected = [];
    for (let item in selectedKeys) {
      selected.push(selectedKeys[item]);
    }
    this.setState({
      selectedKeys,
      selected,
      value: expense,
    });
  };

  handleCategory = () => {
    if (!this.state.categoryType.length) {
      this.setState({ categoryLoading: true });
      httpFetch
        .get(`${config.expenseUrl}/api/expense/types/category`, {
          setOfBooksId: this.props.params.setOfBooksId,
        })
        .then(res => {
          this.setState({
            categoryType: res.data,
            categoryLoading: false,
          });
        });
    }
  };

  handleCategoryChange = value => {
    this.setState(
      {
        searchParams: {
          ...this.state.searchParams,
          categoryType: value,
        },
      },
      this.getList
    );
  };

  handleTypeChange = value => {
    console.log(value);
    this.setState(
      {
        searchParams: {
          ...this.state.searchParams,
          expense: value,
        },
      },
      this.getList
    );
  };

  render() {
    const {
      value,
      visible,
      expenseType,
      loading,
      categoryLoading,
      categoryType,
      selectedKeys,
    } = this.state;
    const { placeholder, title, modalWidth, single } = this.props;

    return (
      <div className="expense-type-selector">
        <Select
          mode={single ? '' : 'multiple'}
          value={value}
          labelInValue
          onDropdownVisibleChange={this.handleFocus}
          placeholder={placeholder}
        />
        <Modal
          title={title}
          visible={visible}
          width={modalWidth}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
        >
          <Spin spinning={loading}>
            <Row gutter={24}>
              <Col span={12}>
                {this.$t('expense.big.type')}:
                <Select
                  loading={categoryLoading}
                  onFocus={this.handleCategory}
                  style={{ width: '75%', marginLeft: 10 }}
                  onChange={this.handleCategoryChange}
                  placeholder={this.$t('adjust.choose.expense.big.type')}
                >
                  {categoryType.map(item => <Option key={item.id}>{item.name}</Option>)}
                </Select>
              </Col>
              <Col span={12}>
                {this.$t('chooser.data.type')}:
                <Input.Search
                  onChange={this.handleTypeChange}
                  style={{ width: '75%', marginLeft: 10 }}
                  placeholder={this.$t('common.search.expense.type')}
                />
              </Col>
            </Row>
            <Row gutter={24} style={{ marginTop: 25 }}>
              {expenseType.map(item => {
                return (
                  <Col onClick={() => this.handleSelect(item)} span={6}>
                    <div
                      style={{
                        padding: 10,
                        background: selectedKeys[item.id] ? '#D2EAF6' : '',
                        border: '1px solid rgb(242,242,242)',
                      }}
                    >
                      <img src={item.iconUrl} />
                      <span>{item.name}</span>
                    </div>
                  </Col>
                );
              })}
            </Row>
          </Spin>
        </Modal>
      </div>
    );
  }
}

ExpenseTypeSelector.propTypes = {
  onSelect: PropTypes.func.isRequired,
  source: PropTypes.string,
  param: PropTypes.any,
  value: PropTypes.oneOfType([PropTypes.object, PropTypes.array]), //single为false时，需要传入数组
  single: PropTypes.bool, //是否为单选 add by mengsha.wang@huilianyi.com
  filter: PropTypes.func,
};

ExpenseTypeSelector.defaultProps = {
  placeholder: '请选择',
  title: '费用类型',
  modalWidth: 800,
  single: true,
  filter: () => true,
};

export default ExpenseTypeSelector;
