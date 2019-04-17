/**
 * 地点级别定义
 * Created by zhanhua.cheng on 2019/3/27.
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import baseService from 'share/base.service';
import config from 'config';
import { Button, Divider, Badge, message } from 'antd';
import NewLocationLevel from 'containers/setting/location-level/new-location-level';

class LocationLevel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      page: 0,
      pageSize: 10,
      pagination: {
        total: 0,
        showQuickJumper: true,
        showSizeChanger: true,
      },
      btnLoad: false,
      columns: [
        {
          title: '账套',
          dataIndex: 'setOfBooksName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '地点级别代码',
          dataIndex: 'code',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '地点级别名称',
          dataIndex: 'name',
          align: 'center',
          width: 100,
        },
        {
          title: '备注',
          dataIndex: 'remarks',
          align: 'center',
          tooltips: true,
          width: 120,
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: 100,
          align: 'center',
          render: enabled => (
            <Badge status={enabled ? 'success' : 'error'} text={enabled ? '启用' : '禁用'} />
          ),
        },
        {
          title: this.$t('common.operation'),
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => {
            return (
              <span>
                <a onClick={() => this.edit(record)}>{this.$t('common.edit')}</a>
                <Divider type="vertical" />
                <a onClick={() => this.distributeClick(record)}>添加地点</a>
              </span>
            );
          },
          width: 200,
        },
      ],
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: this.$t('chooser.data.setOfBooks') /* 账套 */,
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
          colSpan: 6,
          defaultValue: props.company.setOfBooksId,
        },
        {
          type: 'input',
          id: 'code',
          label: '地点级别代码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'name',
          label: '地点级别名称',
          colSpan: 6,
        },
        {
          type: 'select',
          id: 'enabled',
          label: '状态',
          options: [{ value: true, label: '启用' }, { value: false, label: '禁用' }],
          labelKey: 'name',
          valueKey: 'id',
          event: 'enabled',
          colSpan: 6,
        },
      ],
      setOfBooksIdList: [],
      setOfBooksId: Number(props.match.params.setOfBooksId)
        ? props.match.params.setOfBooksId
        : props.company.setOfBooksId,
      showSlideFrame: false,
      params: {},
    };
  }

  componentWillMount() {
    this.getSetOfBookList();
  }

  // 获取账套
  getSetOfBookList = () => {
    baseService
      .getSetOfBooksByTenant()
      .then(res => {
        const { searchForm, setOfBooksId } = this.state;
        const setOfBooksIdList = res.data.map(item => {
          return {
            label: `${item.setOfBooksCode}-${item.setOfBooksName}`,
            value: item.id,
          };
        });
        searchForm[0].options = setOfBooksIdList;
        searchForm[0].defaultValue = setOfBooksId;
        this.setState({ searchForm, setOfBooksIdList });
      })
      .catch(err => message.error(err.response.data.message));
  };

  // 搜索域搜索
  handleSearch = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: { ...searchParams, ...values },
      },
      () => {
        // 调用查询方法
        const { searchParams } = this.state;
        this.table.search(searchParams);
      }
    );
  };

  // 编辑
  edit = record => {
    this.setState({ showSlideFrame: true, params: record });
  };
  // 账套切换事件
  formChange = (event, value) => {
    if (event === 'setOfBooksId') {
      this.setState(
        { setOfBooksId: value, searchParams: { ...this.state.searchParams, setOfBooksId: value } },
        () => {
          this.table.search(this.state.searchParams);
        }
      );
    } else if (event == 'enabled') {
      this.setState({ searchParams: { ...this.state.searchParams, enabled: value } }, () => {
        this.table.search(this.state.searchParams);
      });
    }
  };

  // 重置搜索
  handleClear = () => {
    this.setState({ searchParams: { ...this.state.searchParams } }, () => {
      this.table.search(this.state.searchParams);
    });
  };

  // 新建地点级别
  createHandle = () => {
    this.setState({ showSlideFrame: true });
  };

  // 侧滑框关闭
  close = isRefresh => {
    this.setState({ showSlideFrame: false, params: {} });
    if (isRefresh) {
      this.table.search();
    }
  };

  // 添加地点
  distributeClick = record => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/admin-setting/location-level/distribute-location/${record.id}`,
      })
    );
  };

  render() {
    const {
      btnLoad,
      searchForm,
      columns,
      showSlideFrame,
      params,
      setOfBooksId,
      setOfBooksIdList,
    } = this.state;
    return (
      <div>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.handleClear}
          eventHandle={this.formChange}
          maxLength={4}
        />
        <div>
          <Button loading={btnLoad} type="primary" onClick={this.createHandle}>
            新建地点级别
          </Button>
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.mdataUrl}/api/location/level/query`}
          params={{ setOfBooksId }}
        />
        <SlideFrame
          title={
            params.id
              ? this.$t('编辑地点级别') /* 编辑地点级别 */
              : this.$t('新建地点级别') /* 新建地点级别 */
          }
          show={showSlideFrame}
          onClose={() => this.close()}
        >
          <NewLocationLevel
            params={params}
            setOfBooksIdList={setOfBooksIdList}
            setOfBooksId={setOfBooksId}
            onClose={this.close}
          />
        </SlideFrame>
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(LocationLevel);
