import React, { Component } from 'react';
import { message, Icon, Tabs, Button, Input, Popover } from 'antd';
import config from 'config';
import baseService from 'share/base.service';
import httpFetch from 'share/httpFetch';
import service from './responsibility-service';
import CustomTable from 'components/Widget/custom-table';
import SlideFrame from 'widget/slide-frame';
import Responsibility from './new-responsibility';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import BasicInfo from './basic-info';
import debounce from 'lodash.debounce';
const Search = Input.Search;

class ResponsibilityCenter extends Component {
  constructor(props) {
    super(props);
    this.state = {
      infoList: [
        {
          type: 'input',
          id: 'departmentCode',
          isRequired: true,
          label: '部门代码:',
        },
        {
          type: 'input',
          id: 'name',
          isRequired: true,
          label: '部门名称:',
        },
      ],
      columns: [
        {
          title: '账套',
          dataIndex: 'setOfBooksName',
          align: 'center',
          render: (setOfBooksName, record) => {
            return (
              <span>
                {record.setOfBooksCode}-{record.setOfBooksName}
              </span>
            );
          },
        },
        {
          title: '公司',
          dataIndex: 'companyName',
          align: 'center',
          render: (companyName, record) => {
            return (
              <span>
                {record.companyCode}-{record.companyName}
              </span>
            );
          },
        },
        {
          title: '默认责任中心',
          dataIndex: 'defaultResponsibilityCenterName',
          align: 'center',
          render: (defaultResponsibilityCenterName, record) => {
            return (
              <span>
                {record.defaultResponsibilityCenterCode}-{record.defaultResponsibilityCenterName}
              </span>
            );
          },
        },
        {
          title: '可用责任中心',
          dataIndex: 'allResponsibilityCenterCount',
          align: 'center',
          render: value => {
            return Number(value) ? `已选${value}个` : '全部';
          },
        },
        {
          title: '操作',
          dataIndex: 'id',
          align: 'center',
          render: (value, record, index) => {
            return (
              <span>
                <a
                  onClick={() => {
                    this.edit(record);
                  }}
                >
                  编辑
                </a>
              </span>
            );
          },
        },
      ],
      loading: false,
      updateParams: {},
      showSlideFrame: false,
      allSetBooks: [],
      pagination: { current: 1 },
      searchParams: {},
      toSearchText: '',
      page: 0,
      infoData: {},
      departmentId: this.props.match.params.departmentId,
      departmentOid: this.props.match.params.departmentOid,
    };
  }

  componentDidMount() {
    this.getSetOfBookList();
    this.getDepartment();
  }

  getDepartment = () => {
    const id = this.state.departmentOid;
    service
      .getDimensionDetail(id)
      .then(res => {
        this.setState({
          infoData: res.data,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 获取帐套
  getSetOfBookList = () => {
    const { departmentId } = this.state;
    let url = `${config.mdataUrl}/api/setOfBooks/by/department?departmentId=${departmentId}`;
    httpFetch.get(url).then(res => {
      let list = [];
      res.data.map(item => {
        list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
      });
      this.setState({
        allSetBooks: list,
      });
    });
  };

  // 编辑
  edit = record => {
    this.setState({
      showSlideFrame: true,
      updateParams: JSON.parse(JSON.stringify(record)),
    });
  };

  // 新建
  createResponsion = e => {
    e.preventDefault();
    this.setState({
      updateParams: {},
      showSlideFrame: true,
    });
  };

  handleClose = params => {
    this.setState(
      {
        showSlideFrame: false,
      },
      () => {
        params && this.table.search();
      }
    );
  };

  //返回到组织架构
  onBackClick = e => {
    e.preventDefault();
    this.props.dispatch(
      routerRedux.replace({
        pathname: `/enterprise-manage/org-structure/org-structure`,
      })
    );
  };

  //搜索
  onSearchTestChange = value => {
    this.setState({ toSearchText: value });
  };

  confirmSearch = () => {
    this.table.search({ keyword: this.state.toSearchText });
  };

  render() {
    const {
      infoList,
      infoData,
      columns,
      departmentId,
      updateParams,
      showSlideFrame,
      allSetBooks,
      toSearchText,
    } = this.state;
    return (
      <div>
        <BasicInfo infoList={infoList} infoData={infoData} />
        <Button
          style={{ margin: '30px 0', width: '85px' }}
          className="create-btn"
          type="primary"
          onClick={this.createResponsion}
        >
          新 建
        </Button>
        <Search
          placeholder="请输入账套代码名称"
          value={toSearchText}
          onChange={e => this.onSearchTestChange(e.target.value)}
          onSearch={this.confirmSearch}
          style={{ width: '300px', float: 'right', margin: '30px 0' }}
        />
        <CustomTable
          columns={columns}
          url={`${
            config.mdataUrl
          }/api/department/sob/responsibility/query?departmentId=${departmentId}`}
          ref={ref => (this.table = ref)}
        />
        <SlideFrame
          title={JSON.stringify(updateParams) === '{}' ? '新建责任中心配置' : '编辑责任中心配置'}
          show={showSlideFrame}
          onClose={() => this.setState({ showSlideFrame: false })}
        >
          <Responsibility
            allSetBooks={allSetBooks}
            params={{ ...updateParams }}
            close={this.handleClose}
            departmentId={departmentId}
          />
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
)(ResponsibilityCenter);
