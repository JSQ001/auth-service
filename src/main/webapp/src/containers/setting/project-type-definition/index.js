import React, { Component } from 'react';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import { Row, Button, Badge } from 'antd';
import SlideFrame from 'components/Widget/slide-frame';
import NewDefinition from './new-definition';
import baseService from 'share/base.service';
import config from 'config';

class ProjectTypeDefinition extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'select',
          colSpan: 6,
          id: 'setOfBooksId',
          label: this.$t({ id: 'pre.payment.setOfBookName' } /*账套*/),
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
          defaultValue: props.company.setOfBooksId,
        },
        {
          label: '项目类型代码',
          type: 'input',
          id: 'projectTypeCode',
          colSpan: 6,
        },
        {
          label: '项目类型名称',
          type: 'input',
          id: 'projectTypeName',
          colSpan: 6,
        },
        {
          label: '状态',
          type: 'value_list',
          id: 'status',
          colSpan: 6,
          options: [
            { label: this.$t('common.status.enable'), value: true },
            { label: this.$t('common.status.disable'), value: false },
          ],
          valueKey: 'value',
          labelKey: 'label',
          event: 'enabled',
        },
      ],
      searchParams: {}, // 搜索条件
      columns: [
        {
          title: this.$t('账套'),
          dataIndex: 'setOfBooksName',
          align: 'center',
        },
        {
          title: this.$t('项目类型代码'),
          dataIndex: 'projectTypeCode', //
          align: 'center',
        },
        {
          title: this.$t('项目类型名称'),
          dataIndex: 'projectTypeName', //
          align: 'center',
        },
        {
          /*状态*/
          title: this.$t('状态'),
          key: 'enabled',
          dataIndex: 'enabled',
          render: enabled => (
            <Badge
              status={enabled ? 'success' : 'error'}
              text={
                enabled
                  ? this.$t({ id: 'common.status.enable' })
                  : this.$t({ id: 'common.status.disable' })
              }
            />
          ),
        },
        {
          title: this.$t('common.operation') /*操作*/,
          align: 'center',
          dataIndex: 'operate',
          render: (desc, record) => {
            return (
              <span>
                <a
                  onClick={e => {
                    this.handleEdit(record);
                  }}
                >
                  {this.$t('common.edit')}
                </a>
              </span>
            );
          },
        },
      ],
      showProjectTypeFlag: false,
      record: {},
      slidingButtonType: '',
      list: [],
      setOfBooksId: props.company.setOfBooksId,
    };
  }

  componentDidMount() {
    this.getSetOfBookList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService.getSetOfBooksByTenant().then(res => {
      const { tenantId } = this.props.company;
      let list = [];
      res.data.map(item => {
        list.push({
          value: item.id,
          label: `${item.setOfBooksCode}-${item.setOfBooksName}`,
          tenantId,
        });
      });
      let form = this.state.searchForm;
      form[0].options = list;
      form[0].defaultValue = this.props.company.setOfBooksId;
      console.log(form[0].options, 'form[1].options');
      console.log(form[0].defaultValue, 'form[1].defaultValue');
      this.setState({ searchForm: form, setOfBooksId: form[0].defaultValue, list });
    });
  };

  handleEdit(record) {
    console.log(record);
    this.setState({ showProjectTypeFlag: true, record: record, title: this.$t('编辑项目类型') });
  }

  formChange = (event, value) => {
    if (event == 'setOfBooksId') {
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

  // 重置搜索
  handleClear = () => {
    this.setState({ searchParams: {} }, () => {
      this.table.search();
    });
  };

  handleAdd = () => {
    this.setState({
      showProjectTypeFlag: true,
      record: {},
      title: this.$t('新建项目类型'),
    });
  };

  rowClick = record => {
    this.setState({ record, showProjectTypeFlag: true });
  };

  handleCloseExpense = flag => {
    if (flag) {
      this.table.reload();
    }
    this.setState({ record: {}, showProjectTypeFlag: false });
  };

  render() {
    const {
      columns,
      showProjectTypeFlag,
      title,
      searchForm,
      record,
      setOfBooksId,
      list,
    } = this.state;

    return (
      <div className="project-apply">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          clearHandle={this.handleClear}
          maxLength={4}
          eventHandle={this.formChange}
        />
        <Row style={{ padding: '10px 0' }}>
          <Button type="primary" onClick={this.handleAdd}>
            新建
          </Button>
        </Row>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          onClick={this.rowClick}
          showNumber={true}
          url={`${config.contractUrl}/api/project/type/query`}
          params={{ setOfBooksId }}
        />
        <SlideFrame
          show={showProjectTypeFlag}
          title={title}
          onClose={() => this.setState({ showProjectTypeFlag: false, record: {} })}
          width="800px"
        >
          <NewDefinition
            params={{
              ...record,
              setOfBooksId: setOfBooksId,
              tenantId: this.props.company.tenantId,
              setOfBookList: list,
            }}
            onClose={this.handleCloseExpense}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}
export default connect(mapStateToProps)(ProjectTypeDefinition);
