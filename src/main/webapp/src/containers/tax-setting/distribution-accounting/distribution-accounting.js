/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Badge, Icon } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import CustomTable from 'widget/custom-table';
import NewDistributionAccounting from './new-distribution-accounting';

import 'styles/setting/params-setting/params-setting.scss';
import { Object } from 'core-js';
import { getDate } from 'date-fns';
import distributionAccountingService from './distribution-accounting-service';

class InvoicingDimension extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: '机构代码',
          dataIndex: 'companyCode',
          align: 'center',
        },
        {
          title: '机构名称',
          dataIndex: 'companyName',
          align: 'center',
        },
        {
          title: '责任中心代码',
          dataIndex: 'responsibilityCenterCode',
          align: 'center',
        },

        {
          title: '责任中心名称',
          dataIndex: 'responsibilityCenterName',
          align: 'center',
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
      taxCategory: {},
      selectedKey: [],
      id: props.match.params.taxpayerId,
    };
  }

  componentDidMount() {
    // const { id } = this.props.match.params;
    // distributionAccountingService
    //   .getTaxCategoryById(id)
    //   .then(res => {
    //     this.setState({ taxCategory: res.data });
    //   })
    //   .catch(err => {
    //     message.error(err.response.data.message);
    //   });
  }

  //返回到开票点管理页面
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/basic-tax-information-management/tax-subject-allocation/tax-subject-allocation`,
      })
    );
  };

  // componentDidMount() {
  //   this.setState({
  //     model: { taxCategoryName: this.state.dataSource[0].taxCategoryName }
  //   });
  // }

  //新建
  create = () => {
    const { taxCategory } = this.state;
    this.setState({
      visibel: true,
      model: { taxCategoryId: taxCategory.id, taxCategoryName: taxCategory.taxCategoryName },
    });
  };

  //编辑
  // edit = record => {
  //   console.log(record);
  //   this.setState({ model: { ...record }, visibel: true });
  // };

  //搜索
  // search = values => {
  //   Object.keys(values).map(key => {
  //     if (!values[key]) {
  //       delete values[key];
  //     }
  //   });
  //   this.setState({ searchParams: values, page: 0 }, () => {
  //     // console.log(this.table, this.state.searchParams);
  //     this.table.search(this.state.searchParams);
  //   });
  // };

  //获取列表
  getList = () => {
    let { searchParams } = this.state;
    this.table && this.table.search(searchParams);
    //  getDate().then(res => {
    //     this.setState({
    //         columns:res.data
    //     })
    //  })
    // this.setState({
    //   columns: [
    //     {
    //       taxRate: 1,
    //       taxCategoryName: 1112233,
    //     },
    //   ],
    // });
    // this.table.search(searchParams);
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
    // console.log(selectedKey);
    distributionAccountingService
      .deleteTaxTaxpayerOrgBatch(selectedKey)
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
    // console.log(key)
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
      id,
    } = this.state;
    // let model = this.state.model
    console.log(this.props.match.params);
    const rowSelection = {
      onChange: this.selectChange,
      selectedRowKeys: selectedKey,
    };
    // const {match} = this.props;
    return (
      <div className="taxRate">
        {/* <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.empty} /> */}
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

        <CustomTable
          columns={columns}
          url={`${config.taxUrl}/api/tax/taxpayer/org/pageByCondition?taxpayerId=${id}`}
          ref={ref => (this.table = ref)}
          rowSelection={rowSelection}
          // params={{'taxpayerId':this.props.match.params.id}}
        />
        <SlideFrame
          title={model.id ? '编辑开票权限分配' : '新增开票权限分配'}
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          <NewDistributionAccounting params={model} close={this.close} id={id} />
        </SlideFrame>
        <p style={{ marginBottom: '20px' }}>
          <a onClick={this.onBackClick}>
            <Icon type="rollback" />返回
          </a>
        </p>
      </div>
    );
  }
}

// export default TaxRate;
export default connect()(InvoicingDimension);
