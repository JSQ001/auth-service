import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import 'styles/my-wallet/new-invoice.scss';
import config from 'config';
import { connect } from 'dva';
import { Button, Icon, Badge, message } from 'antd';
import locationLevelService from './service';
import { messages } from 'utils/utils';
import { routerRedux } from 'dva/router';

class AddLocationTab extends Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      btnLoad: false,
      columns: [
        {
          title: '地点编码',
          dataIndex: 'code',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '地点名称',
          dataIndex: 'description',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '属性',
          dataIndex: 'typeDesc',
          align: 'center',
          width: 100,
        },
        {
          title: '国家',
          dataIndex: 'country',
          align: 'center',
          tooltips: true,
          width: 120,
        },
        {
          title: '省/州',
          dataIndex: 'state',
          align: 'center',
          tooltips: true,
          width: 120,
        },
        {
          title: '市/区',
          dataIndex: 'city',
          align: 'center',
          tooltips: true,
          width: 120,
        },
      ],
      searchForm: [
        {
          type: 'select',
          id: 'countryCode',
          label: '国家',
          options: [],
          event: 'countryCode',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'stateCode',
          label: '省/州',
          options: [],
          event: 'stateCode',
          colSpan: 6,
          disabled: true,
        },
        {
          type: 'select',
          id: 'cityCode',
          label: '市/区',
          options: [],
          event: 'cityCode',
          colSpan: 6,
          disabled: true,
        },
        {
          type: 'value_list',
          id: 'type',
          label: '属性',
          options: [],
          valueListCode: 'LOCATION_TYPE',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'code',
          label: '地点编码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'description',
          label: '地点名称',
          colSpan: 6,
        },
      ],
      countryList: [],
      stateList: [],
      cityList: [],
      disabled: true,
      selectedRowKeys: [],
    };
  }

  // 获取国家
  getCountryList = () => {
    locationLevelService
      .getCountryList()
      .then(res => {
        const { searchForm } = this.state;
        const countryList = res.data.map(item => {
          return {
            label: `${item.countryCode}-${item.country}`,
            value: item.countryCode,
          };
        });
        searchForm[0].options = countryList;
        this.setState({ countryList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  componentDidMount() {
    this.getCountryList();
  }

  // 下拉框切换事件
  handleEvent = (event, value) => {
    if (event === 'countryCode') {
      const { searchForm } = this.state;
      this.setState({ searchForm });
      let countryCode = value;
      locationLevelService
        .getStateListByCountryCode(countryCode)
        .then(res => {
          const stateList = res.data.map(item => {
            return {
              label: `${item.stateCode}-${item.state}`,
              value: item.stateCode,
            };
          });
          searchForm[1].options = stateList;
          searchForm[1].disabled = false;
          this.setState({ stateList });
          this.formRef.setValues({
            cityCode: '',
          });
        })
        .catch(err => message.error(err.response.data.message));
    }
    if (event === 'stateCode') {
      const { searchForm } = this.state;
      this.setState({ searchForm });
      let stateCode = value;
      locationLevelService
        .getCityListByStateCode(stateCode)
        .then(res => {
          const cityList = res.data.map(item => {
            return {
              label: `${item.cityCode}-${item.city}`,
              value: item.cityCode,
            };
          });
          console.log(cityList);
          searchForm[2].options = cityList;
          searchForm[2].disabled = false;
          this.setState({ cityList });
        })
        .catch(err => message.error(err.response.data.message));
    }
  };

  // 搜索
  search = params => {
    const data = {
      ...params,
    };
    this.table.search(data);
  };

  //勾选地点，多选
  onSelectChange = selectedRowKeys => {
    this.setState({ selectedRowKeys }, () => {
      selectedRowKeys.length > 0
        ? this.setState({ disabled: false })
        : this.setState({ disabled: true });
    });
  };

  // 批量添加地点
  batchAddLocation = () => {
    const { selectedRowKeys } = this.state;
    const { levelId } = this.props.params;
    let paramsValue = {};
    paramsValue.levelId = levelId;
    paramsValue.locationIds = selectedRowKeys;
    if (paramsValue.locationIds.length === 0) {
      message.warn(this.$t('common.select.one.more'));
      return;
    }
    locationLevelService
      .distributeLocation(paramsValue)
      .then(res => {
        if (res.status === 200) {
          message.success(this.$t('common.operate.success'));
          this.props.onAddSuccess();
          this.table.search();
          this.setState({
            selectedRowKeys: [],
            disabled: true,
          });
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`${this.$t('common.operate.filed')}，${e.response.data.message}`);
        }
      });
  };
  // 返回
  back = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/admin-setting/location-level/location-level',
      })
    );
  };

  render() {
    const { searchForm, columns, selectedRowKeys, disabled } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    return (
      <div style={{ marginTop: '15px' }}>
        <SearchArea
          searchForm={searchForm}
          eventHandle={this.handleEvent}
          submitHandle={this.search}
          maxLength={4}
          wrappedComponentRef={inst => (this.formRef = inst)}
        />
        <Button
          type="primary"
          style={{ marginBottom: '15px' }}
          disabled={disabled}
          onClick={this.batchAddLocation}
        >
          添加地点
        </Button>
        <CustomTable
          tableKey="locationId"
          columns={columns}
          ref={ref => (this.table = ref)}
          url={`${config.mdataUrl}/api/location/level/query/location`}
          rowSelection={rowSelection}
        />
        <a className="back" onClick={this.back}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />
          {messages('common.back') /*返回*/}
        </a>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(mapStateToProps)(AddLocationTab);
