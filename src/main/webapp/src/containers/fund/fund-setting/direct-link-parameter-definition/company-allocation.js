import React from 'react';
import { Row, Col, Checkbox, Badge, Button, Icon, message } from 'antd';
import Table from 'widget/table';
import ListSelector from 'widget/list-selector';
import config from 'config';
import service from './direct-link-parameter-definition.service';
import 'styles/fund/account.scss';

class CompanyAllocation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      pagination: {
        total: 0,
        page: 0,
        pageSize: 10,
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`,
      },
      headPreviewData: {},
      headPreviewItems: [
        { label: '银行名称', id: 'bankName', key: '1' },
        { label: '银行代码', id: 'bankCode', key: '2' },
        { label: '直联描述', id: 'description', key: '3' },
        { label: '状态', id: 'enabled', key: '4' },
      ],
      // 表格
      columns: [
        { title: '公司代码', dataIndex: 'companyCode' },
        { title: '公司名称', dataIndex: 'companyName' },
        {
          title: '公司类型',
          dataIndex: 'companyTypeName',
        },
        {
          title: '启用',
          dataIndex: 'enabled',
          width: '8%',
          align: 'center',
          render: (enabled, record) => (
            <Checkbox defaultChecked={enabled} onChange={e => this.handleStatusChange(e, record)} />
          ),
        },
      ],

      selectorItem: {
        title: '批量分配公司',
        url: `${config.fundUrl}/api/bankParams/company/filter?headId=${props.match.params.id}`,
        searchForm: [
          { type: 'input', id: 'companyCode', label: '公司代码' },
          { type: 'input', id: 'companyName', label: '公司名称' },
          { type: 'input', id: 'companyCodeFrom', label: '公司代码从' },
          { type: 'input', id: 'companyCodeTo', label: '公司代码至' },
        ],
        columns: [
          { title: '公司代码', dataIndex: 'companyCode' },
          { title: '公司名称', dataIndex: 'name' },
          {
            title: '公司类型',
            dataIndex: 'companyTypeName',
            render: value => value,
          },
        ],
        key: 'id',
      },
    };
  }

  componentDidMount() {
    this.getheadPreviewData();
    this.getList();
  }

  /**
   * 获取头
   */
  async getheadPreviewData() {
    const { match } = this.props;
    await service.getHead(0, 10, '', match.params.id).then(response => {
      this.setState({
        headPreviewData: response.data[0],
      });
    });
  }

  /**
   * 获取table列表
   */
  getList = async () => {
    const { match } = this.props;
    const { pagination } = this.state;
    this.setState({ loading: true });
    await service
      .getCompanys(pagination.page, pagination.pageSize, match.params.id)
      .then(response => {
        this.setState({
          tableData: response.data,
          loading: false,
        });
      });
  };

  /**
   * 切换启用状态
   */
  handleStatusChange = (e, record) => {
    const params = [
      {
        id: record.id,
        headId: record.headId,
        companyId: record.companyId,
        enabled: e.target.checked ? 1 : 0,
      },
    ];
    service
      .insertCompanies(params)
      .then(response => {
        if (response.status === 200) {
          message.success('操作成功');
          this.getList();
        }
      })
      .catch(error => {
        message.error(error.response.data.message);
      });
  };

  handleListShow = value => {
    this.setState({
      showListSelector: value,
    });
  };

  handleListOk = values => {
    const { match } = this.props;
    const params = [];
    let copyValues = values;
    if (!copyValues.result || !copyValues.result.length) {
      this.handleListShow(false);
      return;
    }
    copyValues = copyValues.result.map(item => item.id); // 单选、多选，获取选中的公司ID
    copyValues.forEach(item => {
      params.push({
        headId: match.params.id,
        companyId: item,
      });
    });
    service
      .insertCompanies(params)
      .then(response => {
        if (response.status === 200) {
          message.success('操作成功');
          this.handleListShow(false);
          this.getList();
        }
      })
      .catch(e => {
        if (e.response) {
          message.error(`操作失败，${e.response.data.message}`);
        }
      });
  };

  render() {
    const {
      headPreviewData,
      showListSelector,
      headPreviewItems,
      tableData,
      loading,
      columns,
      pagination,
      selectorItem,
    } = this.state;
    const headPreview = [];
    headPreviewItems.forEach(item => {
      headPreview.push(
        <Col span={6} key={item.key}>
          <div style={{ color: '#989898' }}>{item.label}</div>
          <div style={{ wordWrap: 'break-word' }}>
            {item.id === 'enabled' ? (
              <Badge
                status={headPreviewData.enabled === 1 ? 'success' : 'error'}
                text={headPreviewData.enabled === 1 ? '启用' : '禁用'}
              />
            ) : (
              headPreviewData[item.id]
            )}
          </div>
        </Col>
      );
    });

    return (
      <div>
        <Row style={{ background: '#f7f7f7', padding: '20px', borderRadius: '6px' }}>
          {headPreview}
        </Row>
        <div className="train">
          <div className="table-header">
            <div className="table-header-buttons">
              <Row>
                <Col span={8}>
                  <Button type="primary" onClick={() => this.handleListShow(true)}>
                    分配公司
                  </Button>
                </Col>
              </Row>
            </div>
          </div>
        </div>

        <Table
          rowKey={record => record.companyId}
          columns={columns}
          dataSource={tableData}
          loading={loading}
          onChange={this.onChangePager}
          bordered
          pagination={pagination}
          size="middle"
        />
        <ListSelector
          visible={showListSelector}
          onCancel={() => this.handleListShow(false)}
          selectorItem={selectorItem}
          onOk={this.handleListOk}
        />
        <a style={{ fontSize: '14px', paddingBottom: '20px' }} onClick={this.handleBack}>
          <Icon type="rollback" style={{ marginRight: '5px' }} />返回
        </a>
      </div>
    );
  }
}

export default CompanyAllocation;
