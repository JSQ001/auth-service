import React from 'react';
import { Row, Table, Badge, Popconfirm, Button, message } from 'antd';
import { connect } from 'dva';
import SlideFrame from 'components/Widget/slide-frame';
import BSService from 'containers/basic-data/bank-definition/bank-definition.service';
import FundSearchForm from '../../fund-components/fund-search-form';
import cshBankNumbersService from './csh_bank_numbers.service';
import CshBankNumbersMaintain from './csh_bank_numbers_maintain';

class CshBankNumbers extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false, // loading状态
      editModel: {
        params: {},
        countryData: {},
      }, // 点击行时的编辑数据
      tableData: [],
      country: [], // 需要传入侧边栏的数据
      slideVisible: false, // 侧边栏显示
      searchForm: [
        // 银行名称
        {
          colSpan: 6,
          type: 'valueList',
          label: '银行名称',
          id: 'bankCode',
          options: [],
          valueListCode: 'ZJ_OPEN_BANK',
        },
        // 分支行名称
        {
          colSpan: 6,
          type: 'input',
          label: '分支行名称',
          id: 'bankName',
        },
        // 联行号
        {
          colSpan: 6,
          type: 'input',
          label: '联行号',
          id: 'bankNumber',
        },
        // 开户地
        {
          colSpan: 6,
          type: 'input',
          label: '开户地',
          id: 'openAddress',
        },
      ],
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      // 列表columns
      columns: [
        {
          title: '国家',
          dataIndex: 'countryName',
          width: 120,
          align: 'center',
        },
        {
          title: '银行名称',
          dataIndex: 'bankCodeName',
          width: 200,
          align: 'center',
        },
        {
          title: '联行号',
          dataIndex: 'bankNumber',
          width: 200,
          align: 'center',
        },
        {
          title: '分支行名称',
          dataIndex: 'bankName',
          width: 400,
          align: 'center',
        },
        {
          title: '开户地',
          dataIndex: 'openAddress',
          width: 150,
          align: 'center',
        },

        {
          title: 'Swift Code',
          dataIndex: 'swiftCode',
          width: 100,
          align: 'center',
        },
        {
          title: '详细地址',
          dataIndex: 'bankAddress',
          width: 100,
          align: 'center',
        },
        {
          /* 状态 */
          title: this.$t('common.column.status'),
          key: 'status',
          width: '100',
          dataIndex: 'enabledFlag',
          render: enabledFlag => (
            <Badge
              status={enabledFlag ? 'success' : 'error'}
              text={
                enabledFlag ? this.$t('common.status.enable') : this.$t('common.status.disable')
              }
            />
          ),
        },
        {
          /* 操作 */
          title: this.$t('common.operation'),
          key: 'operation',
          dataIndex: 'operation',
          width: '100',
          render: (text, record) => (
            <span>
              <a onClick={e => this.editItem(e, record)}>{this.$t('common.edit')}</a>
              <span className="ant-divider" />
              <Popconfirm
                onConfirm={e => this.deleteItem(e, record)}
                title={this.$t('budget.are.you.sure.to.delete.rule', {
                  controlRule: record.controlRuleName,
                })}
              >
                {/* 你确定要删除organizationName吗 */}
                <a
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                  }}
                >
                  {this.$t('common.delete')}
                </a>
              </Popconfirm>
            </span>
          ),
        },
      ],
    };
  }

  componentDidMount() {
    this.getList();
    this.getCountrys();
  }
  /* eslint-disable */
  editItem = (e, record) => {
    const editModel1 = {
      params: {},
      countryData: {},
    };
    const { country } = this.state;
    e.preventDefault();
    e.stopPropagation();
    editModel1.params = record;
    editModel1.countryData = country;
    this.setState({
      slideVisible: true,
      editModel: editModel1,
    });
  };

  // 删除
  deleteItem = (e, record) => {
    this.setState({ loading: true });
    // const {pagination} = this.state;
    cshBankNumbersService
      .deleteList(record.id)
      .then(response => {
        // this.setState({
        //   pagination: {
        //     ...pagination,
        //     total: pagination.total - 1,
        //     page:
        //       parseInt((pagination.total - 2) / pagination.pageSize) < pagination.page
        //         ? parseInt((pagination.total - 2) / pagination.pageSize)
        //         : pagination.page,
        //     current:
        //       parseInt((pagination.total - 2) / pagination.pageSize) < pagination.page
        //         ? parseInt((pagination.total - 2) / pagination.pageSize) + 1
        //         : pagination.page + 1,
        //   },
        // });
        this.getList();
      })
      .catch(e => {
        this.setState({ loading: false });
      });
  };

  /**
   * 关闭侧边栏
   */
  handleClose = value => {
    this.setState({
      slideVisible: false,
      editModel: {},
    });
    if (value === 'save') {
      this.getList();
    }
  };

  /**
   * 新建
   */
  create = () => {
    this.setState({ slideVisible: true });
    const editModel1 = {
      params: {},
      countryData: {},
    };
    const { country } = this.state;
    editModel1.countryData = country;
    this.setState({
      editModel: editModel1,
    });
  };

  // 获取列表
  getList = () => {
    const { pagination, searchParams } = this.state;
    this.setState({ loading: true });
    cshBankNumbersService
      .getCshBankNumbersList(pagination.page, pagination.pageSize, searchParams)
      .then(response => {
        const { data } = response;
        this.setState({
          tableData: data,
          loading: false,
          pagination: {
            ...pagination,
            total: Number(response.headers['x-total-count'])
              ? Number(response.headers['x-total-count'])
              : 0,
            onChange: this.onChangePager,
            current: pagination.page + 1,
          },
        });
      });
  };

  /**
   * 新建或者维护保存
   */
  save = params => {
    cshBankNumbersService
      .createOrUpdate(params)
      .then(res => {
        if (res.status === 200) {
          message.success('保存成功！');
          // this.onFlash(res.data.id);
          this.setState({ slideVisible: false });
          this.getList();
        }
      })
      .catch(error => {
        message.error(error.errorCode);
      });
  };

  getCountrys = () => {
    BSService.getCountries(this.props.language.local).then(response => {
      let country = response.data.map(item => {
        item.label = item.country;
        item.value = item.code;
        if (item.value === 'CHN000000000') {
          item.children = [];
        }
        return item;
      });
      this.setState({
        country,
      });
      this.getChinaState();
    });
  };

  // ---------分割线------获取省市------
  // 我这边就占时循环调接口，把省市掉出来，以此添加用户体验
  // 获取中国的所有省
  getChinaState = () => {
    cshBankNumbersService.getCityCode().then(response => {
      const children = response.data.map(item => {
        item.label = item.provinceDesc;
        item.value = item.provinceCode;
        item.children.map(city => {
          city.label = city.description;
          city.value = city.regionCode;
        });
        //item.children = [];
        //this.getCityByCode(item.code);
        return item;
      });
      this.setChinaState(children);
    });
  };

  // 设置国家的省
  setChinaState = children => {
    let countrys = this.state.country;
    for (let i = 0; i < countrys.length; i++) {
      if (countrys[i].code === 'CHN000000000') {
        countrys[i].children = children;
      }
    }
  };
  // ---------分割线------获取省市------

  // 分页点击
  onChangePager = (pagination, filters, sorter) => {
    this.setState(
      {
        pagination: {
          current: pagination.current,
          page: pagination.current - 1,
          pageSize: pagination.pageSize,
          total: pagination.total,
        },
      },
      () => {
        this.getList();
      }
    );
  };

  // 搜索
  search = values => {
    this.setState(
      {
        searchParams: {
          bankCode: values.bankCode ? values.bankCode.key : '', // 银行代码
          bankName: values.bankName ? values.bankName : '', // 分支行名称
          bankNumber: values.bankNumber ? values.bankNumber : '', // 联行号
          openAddress: values.openAddress ? values.openAddress : '', // 地址
        },
      },
      () => {
        this.getList();
      }
    );
  };

  render() {
    const {
      searchForm,
      pagination,
      columns,
      loading,
      tableData,
      slideVisible,
      editModel,
    } = this.state;
    return (
      <div>
        <Row>
          <FundSearchForm searchForm={searchForm} submitHandle={this.search} />
        </Row>
        <div className="table-header">
          <div className="table-header-title">
            {this.$t('common.total', { total: `${pagination.total}` })}
          </div>
          {/* 共搜索到*条数据 */}
          {/* <div className="table-header-buttons">{this.renderBtns(label)}</div> */}
        </div>
        <div className="table-header">
          <div className="table-header-buttons">
            {/*  新建  */}
            <Button type="primary" onClick={() => this.create()}>
              {this.$t('common.create')}
            </Button>
          </div>
        </div>
        <Table
          // onRow={record => {
          //   return {
          //     onClick: () => {
          //       this.setState({ editModel: record, slideVisible: true });
          //     },
          //   };
          // }}
          dataSource={tableData}
          loading={loading}
          pagination={pagination}
          onChange={this.onChangePager}
          columns={columns}
          size="middle"
          scroll={{ x: 1500 }}
          bordered
        />
        <SlideFrame
          title={editModel.params ? '编辑' : '新建'}
          show={slideVisible}
          onClose={this.handleClose}
        >
          <CshBankNumbersMaintain save={this.save} params={editModel} onClose={this.handleClose} />
        </SlideFrame>
      </div>
    );
  }
}

function show(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
    language: state.languages,
    organization: state.user.organization,
  };
}
export default connect(show)(CshBankNumbers);
