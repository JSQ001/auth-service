/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Badge } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import WrappedTaxDefinition from './new-tax-definition';
import CustomTable from 'widget/custom-table';
import NewTaxDifinition from './new-tax-definition';

import 'styles/setting/params-setting/params-setting.scss';
import { Object } from 'core-js';

class TaxDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'taxCategoryCode',
          placeholder: '请输入',
          label: '税种代码',
          colSpan: 6,
        },

        {
          type: 'input',
          id: 'taxCategoryName',
          placeholder: '请输入',
          label: '税种名称',
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '税种代码',
          dataIndex: 'taxCategoryCode',
          align: 'center',
        },
        {
          title: '税种名称',
          dataIndex: 'taxCategoryName',
          align: 'center',
        },
        {
          title: '税率',
          dataIndex: 'taxRate',
          align: 'center',
          render: (value, record) => (
            <a onClick={() => this.toTaxRate(record.id, record.taxCategoryName)}>税率</a>
          ),
        },
        {
          /*状态*/
          title: '状态',
          dataIndex: 'enabled',
          align: 'center',
          render: value => {
            return (
              <div>
                <Badge status={value ? 'success' : 'error'} />
                {value ? this.$t('common.status.enable') : this.$t('common.status.disable')}
              </div>
            );
          },
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'center',
          render: (value, record) => {
            return <a onClick={() => this.edit(record)}>编辑</a>;
          },
        },
      ],
      searchParams: {},
      visibel: false,
      model: {},
    };
  }

  componentWillMount() {
    this.getList();
  }

  // 跳转税率页面
  toTaxRate = (id, taxCategoryName) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/basic-tax-information-management/tax-definition/tax-rate/${id}`,
      })
    );
  };
  // 新建
  create = () => {
    this.setState({
      visibel: true,
    });
  };

  //编辑
  edit = record => {
    console.log(record);
    this.setState({ model: JSON.parse(JSON.stringify(record)), visibel: true });
  };
  //params: {enabled: '',} this.props.params.naeme

  //搜索
  search = values => {
    Object.keys(values).map(key => {
      if (!values[key]) {
        delete values[key];
      }
    });
    this.setState({ searchParams: values, page: 0 }, () => {
      console.log(this.table, this.state.searchParams);
      this.table.search(this.state.searchParams);
    });
  };

  //获取列表
  getList = () => {
    let { searchParams } = this.state;
    console.log(this.table);
    this.table && this.table.search(searchParams);
    //   this.setState({
    //     columns:[
    //         {
    //             taxRate:1,
    //             taxCategoryName:1112233,
    //         }
    //     ]
    // })
    // data this.state({
    //   model:{
    //     taxCategoryName:res.data[0].taxCategoryName,
    //   }
    // })
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

  render() {
    const { searchForm, columns, data, loading, visibel, pagination, model } = this.state;

    return (
      <div className="taxDefinition">
        <SearchArea searchForm={searchForm} submitHandle={this.search} clearHandle={this.empty} />
        <Button
          style={{ margin: '20px 0' }}
          className="create-btn"
          type="primary"
          onClick={this.create}
        >
          新建
        </Button>
        <CustomTable
          columns={columns}
          url={`${config.taxUrl}/api/tax/category/pageByCondition`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={model.id ? '编辑税种' : '新增税种'}
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          <NewTaxDifinition params={model} close={this.close} />
        </SlideFrame>
      </div>
    );
  }
}
// export default  TaxDefinition;

// export default TaxDefinition;

export default connect()(TaxDefinition);

// export default connect(
//   mapStateToProps,
//   null,
//   null,
//   { withRef: true }
//   )(TaxDefinition);
