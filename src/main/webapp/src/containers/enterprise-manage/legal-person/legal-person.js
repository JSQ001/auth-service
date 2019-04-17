import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import config from 'config';
import { Button, Badge, Tooltip, Icon, Popover } from 'antd';
import CustomTable from 'widget/custom-table';
import SearchArea from 'components/Widget/search-area.js';

class LegalPerson extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      data: [],
      //搜索关键字
      params: {
        keyword: '',
      },
      searchForm: [
        {
          type: 'input',
          key: 'keyword', //必填，唯一，每行的标识
          id: 'keyword',
          label: this.$t('legal.person.name'), //'法人实体名称',
        },
      ],
      //老集团表格
      columns: [
        {
          title: this.$t('legal.person.name'), // "法人实体名称",
          align: 'center',
          key: 'entityName',
          dataIndex: 'entityName',
          render: text => (
            <span>
              {text ? (
                <Popover placement="topLeft" content={text}>
                  {text}
                </Popover>
              ) : (
                '-'
              )}
            </span>
          ),
        },
        {
          title: this.$t('legal.person.person.total'), //"员工数量",
          align: 'center',
          key: 'userAmount',
          dataIndex: 'userAmount',
        },
        {
          title: this.$t('legal.person.status'), //'状态',
          align: 'center',
          dataIndex: 'enabled',
          width: '15%',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? this.$t('common.enabled') : this.$t('common.disabled')}
            />
          ),
        },
        {
          title: this.$t('common.operation'), //"操作",
          align: 'center',
          dataIndex: 'id',
          key: 'id',
          render: (text, record) => (
            <span>
              <a
                onClick={e => this.editItemLegalPerson(e, record)}
                disabled={!this.props.tenantMode}
              >
                {/*编辑*/}
                {this.$t('common.edit')}
              </a>
              &nbsp;&nbsp;&nbsp;
              <a onClick={e => this.handleRowClick(e, record)}>
                {/*详情*/}
                {this.$t('common.detail')}
              </a>
            </span>
          ),
        },
      ],
      //新集团表格
      columnsNew: [
        {
          title: this.$t('legal.person.name'), // "法人实体名称",
          key: 'entityName',
          align: 'center',
          dataIndex: 'entityName',
        },
        {
          title: this.$t('legal.person.status'), //'状态',
          align: 'center',
          dataIndex: 'enabled',
          width: '15%',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={enabled ? this.$t('common.enabled') : this.$t('common.disabled')}
            />
          ),
        },
        {
          title: this.$t('common.operation'), //"操作",
          align: 'center',
          dataIndex: 'id',
          key: 'id',
          render: (text, record) => (
            <span>
              <a
                onClick={e => this.editItemLegalPerson(e, record)}
                disabled={!this.props.tenantMode}
              >
                {/*编辑*/}
                {this.$t('common.edit')}
              </a>
              &nbsp;&nbsp;&nbsp;
              <a onClick={e => this.handleRowClick(e, record)}>
                {/*详情*/}
                {this.$t('common.detail')}
              </a>
            </span>
          ),
        },
      ],
    };
  }

  componentDidMount() {
    this.getLegalPersonList();
  }

  //获取法人实体表格
  getLegalPersonList = () => {
    this.setState({
      loading: true,
    });
    let params = {
      keyword: this.state.params.keyword,
    };
    this.table.search(params);
  };
  //点击搜搜索
  handleSearch = values => {
    this.setState(
      {
        params: values,
      },
      () => {
        this.getLegalPersonList();
      }
    );
  };
  //点击情况搜索
  clearSearchHandle = values => {
    let params = {
      keyword: '',
    };
    this.setState(
      {
        params,
      },
      () => {
        this.getLegalPersonList();
      }
    );
  };
  //新增法人实体
  handleCreateLP = () => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/legal-person/new-legal-person/:legalPersonOid/${
          this.props.company.legalEntityId
        }`,
      })
    );
  };
  //编辑法人实体
  editItemLegalPerson = (e, record) => {
    e.stopPropagation();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/legal-person/edit-legal-person/${
          record.companyReceiptedOid
        }/${record.id}`,
      })
    );
  };

  //点击行，进入该行详情页面
  //为了适应新老集团，这里传两个参数
  handleRowClick = (e, record) => {
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/legal-person/legal-person-detail/${
          record.companyReceiptedOid
        }/${record.id}`,
      })
    );
  };

  renderCreateBtn = () => {
    return (
      <div className="table-header-buttons">
        <Button type="primary" disabled={!this.props.tenantMode} onClick={this.handleCreateLP}>
          {/*新增法人实体*/}
          {this.$t('legal.person.new')}
        </Button>
        <Tooltip
          title={
            <div>
              <p>
                {this.$t('legal.person.tips1')}
                {/*1.法人实体名称是员工在法律上归属的公司注册名称,*/}
                {/*在应用开票平台开具增值税发票时,作为选择开票费用集合的单位*/}
              </p>
              <p>
                {this.$t('legal.person.tips2')}
                {/*2.法人实体详细信息是员工个人开具增值税发票的必要信息,*/}
                {/*用户可在APP我的-开票信息中查看*/}
              </p>
            </div>
          }
        >
          <span>
            <Icon type="info-circle-o" />
          </span>
        </Tooltip>
      </div>
    );
  };
  render() {
    return (
      <div className="legal-person-wrap">
        <SearchArea
          searchForm={this.state.searchForm}
          clearHandle={this.clearSearchHandle}
          submitHandle={this.handleSearch}
        />

        <div className="table-header">{this.renderCreateBtn()}</div>

        <CustomTable
          ref={ref => (this.table = ref)}
          columns={this.props.isOldCompany ? this.state.columns : this.state.columnsNew}
          url={`${config.mdataUrl}/api/v2/my/company/receipted/invoices`}
          showNumber
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    profile: state.user.profile,
    user: state.user.currentUser,
    company: state.user.company,
    isOldCompany: state.user.isOldCompany,
    tenantMode: true,
  };
}

LegalPerson.propTypes = {};

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(LegalPerson);
