/* eslint-disable */
import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import { Button, Divider, message, Popconfirm, Badge, Icon } from 'antd';
import SlideFrame from 'widget/slide-frame';
import config from 'config';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import WrappedTaxRate from './new-tax-rate';
import CustomTable from 'widget/custom-table';
import NewTaxRate from './new-tax-rate';

import 'styles/setting/params-setting/params-setting.scss';
import { Object } from 'core-js';
import { getDate } from 'date-fns';
import service from './tax-rate.service';
import taxDefinitionService from '../tax-definition/tax-definition.service';

class TaxRate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          id: 'taxRate',
          placeholder: '请输入',
          label: '税率描述',
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '税率描述',
          dataIndex: 'taxRate',
          align: 'center',
        },
        {
          title: '计税税率',
          dataIndex: 'taxRatio',
          align: 'center',
          render: desc => (desc ? desc + '%' : '-'),
        },
        {
          title: '优惠税率',
          dataIndex: 'preferentialTaxRate',
          align: 'center',
          render: desc => (desc ? desc + '%' : '-'),
        },

        {
          title: '税种',
          dataIndex: 'taxCategoryName',
          align: 'center',
          // render: () => {
          //   return (<span>{decodeURIComponent(props.match.params.taxCategoryName)}</span>)
          // }
        },
        {
          title: '备注',
          dataIndex: 'remarks',
          align: 'center',
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
      taxCategory: {},
    };
  }

  componentDidMount() {
    const { id } = this.props.match.params;

    taxDefinitionService
      .getTaxCategoryById(id)
      .then(res => {
        this.setState({ taxCategory: res.data });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }

  //返回到税种定义
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/basic-tax-information-management/tax-definition/tax-definition`,
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
  edit = record => {
    console.log(record);
    this.setState({ model: { ...record }, visibel: true });
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

  render() {
    const { searchForm, columns, data, loading, visibel, pagination, model, isNew } = this.state;
    // let model = this.state.model
    console.log(this.props.match.params);
    // const {match} = this.props;
    return (
      <div className="taxRate">
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
          url={`${config.taxUrl}/api/tax/rate/details/${this.props.match.params.id}`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={model.id ? '编辑税率' : '新增税率'}
          show={visibel}
          onClose={() => {
            this.setState({
              visibel: false,
              model: {},
            });
          }}
        >
          <NewTaxRate params={model} close={this.close} />
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
export default connect()(TaxRate);
