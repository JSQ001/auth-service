/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Badge, Icon } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import CustomTable from 'widget/custom-table';
import NewClientOther from './new-client-other';

import 'styles/setting/params-setting/params-setting.scss';
import { Object } from 'core-js';
import { getDate } from 'date-fns';
import clientOtherService from './client-other.service';

class ClientOther extends Component {
  constructor(props) {
    super(props);
    this.state = {
      //   searchForm: [
      //     {
      //       type: 'input',
      //       id: 'taxRate',
      //       placeholder: '请输入',
      //       label: '税率',
      //       colSpan: 6,
      //     },
      //   ],
      columns: [
        {
          title: '维度代码',
          dataIndex: 'dimensionCode',
          align: 'center',
        },
        {
          title: '维度名称',
          dataIndex: 'dimensionName',
          align: 'center',
        },
        {
          title: '维值代码',
          dataIndex: 'dimensionValueCode',
          align: 'center',
        },

        {
          title: '维值名称',
          dataIndex: 'dimensionValueName',
          align: 'center',
          // render: () => {
          //   return (<span>{decodeURIComponent(props.match.params.taxCategoryName)}</span>)
          // }
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      selectedKey: [],
      // id: props.match.params.applicationId,
    };
  }

  componentDidMount() {
    // ClientOtherService
    //   .getTaxCategoryById(id)
    //   .then(res => {
    //     this.setState({ taxCategory: res.data });
    //   })
    //   .catch(err => {
    //     message.error(err.response.data.message);
    //   });
  }

  //返回到客户管理
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/basic-tax-information-management/tax-client-application/tax-client`,
      })
    );
  };

  //新建
  create = () => {
    this.setState({
      visibel: true,
      model: { applicationId: this.props.applicationId },
    });
  };

  //编辑
  edit = record => {
    this.setState({ model: { ...record }, visibel: true });
  };

  //搜索
  search = values => {
    Object.keys(values).map(key => {
      if (!values[key]) {
        delete values[key];
      }
    });
    this.setState({ searchParams: values, page: 0 }, () => {
      this.table.search(this.state.searchParams);
    });
  };

  //获取列表
  getList = () => {
    let { searchParams } = this.state;
    this.table && this.table.search(searchParams);
    this.table.search(searchParams);
  };

  //关闭侧拉框回调
  close = flag => {
    this.setState({ visibel: false, model: {} }, () => {
      if (flag) {
        this.getList();
      }
    });
  };

  empty = () => {
    this.search({});
  };

  delete = () => {
    const { selectedKey } = this.state;
    clientOtherService
      .deleteClientOtherBatch(selectedKey)
      .then(() => {
        message.success('删除成功');
        this.setState({ selectedKey: [] });
        this.getList();
      })
      .catch(err => message.warning(err.response.data.message));
  };

  deleteClick = () => {
    const { selectedKey } = this.state;
    if (!selectedKey.length) {
      message.warning('请选择要删除的数据！');
    }
  };

  selectChange = key => {
    this.setState({ selectedKey: key });
  };

  render() {
    const {
      searchForm,
      columns,
      data,
      loading,
      visibel,
      pagination,
      model,
      selectedKey,
    } = this.state;
    const rowSelection = {
      onChange: this.selectChange,
    };

    const buttonShow = (
      <div>
        <Button style={{ margin: '20px 20px 20px 0' }} type="primary" onClick={this.create}>
          新增
        </Button>
        <Button
          style={{ margin: '20px 0', padding: '0' }}
          type="primary"
          onClick={this.deleteClick}
        >
          {selectedKey.length ? (
            <Popconfirm
              title="你确定要删除吗？"
              onConfirm={this.delete}
              okText="确定"
              cancelText="取消"
            >
              <div style={{ lineHeight: '20px', padding: '0 15px' }}>删除</div>
            </Popconfirm>
          ) : (
            <div style={{ lineHeight: '20px', padding: '0 15px' }}>删除</div>
          )}
        </Button>
      </div>
    );
    // const {match} = this.props;
    return (
      <div className="taxClientOther">
        {/* <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.empty} /> */}

        {this.props.flag === false ? buttonShow : <div />}
        <CustomTable
          columns={columns}
          url={`${config.taxUrl}/api/tax/client/application/other/pageByCondition?applicationId=${
            this.props.applicationId
          }`}
          ref={ref => (this.table = ref)}
          rowSelection={rowSelection}
        />
        <SlideFrame
          title={model.id ? '编辑客户权限' : '新增客户权限'}
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          <NewClientOther params={model} close={this.close} />
        </SlideFrame>
      </div>
    );
  }
}

// export default TaxRate;
export default connect()(ClientOther);
