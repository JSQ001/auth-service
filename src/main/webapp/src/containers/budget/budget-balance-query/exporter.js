import React from 'react';
import { connect } from 'dva';
import { Modal, Tabs, message, Radio } from 'antd';
import Table from 'widget/table';
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
import budgetBalanceService from 'containers/budget/budget-balance/budget-balance.service';
import FileSaver from 'file-saver';
import PropTypes from 'prop-types';

//数据导入组件
class Exporter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      exporting: false,
      selectedRowKeys: [],
      excelVersion: '2003',
    };
  }

  exportResult = () => {
    let { selectedRowKeys, excelVersion } = this.state;
    if (selectedRowKeys.length === 0) {
      message.error('请选择导出列');
      return;
    }
    let columnFiledMap = {};
    this.props.columns.map((column, index) => {
      if (selectedRowKeys.indexOf(column.dataIndex) > -1) columnFiledMap['' + index] = column.title;
    });
    this.props.dimensionColumns.map(column => {
      if (selectedRowKeys.indexOf(column.dataIndex) > -1)
        columnFiledMap[20 + Number(column.index) + ''] = column.title;
    });
    let hide = message.loading(this.$t({ id: 'importer.spanned.file' } /*正在生成文件..*/));
    this.setState({ exporting: true });
    budgetBalanceService
      .exportBalance({
        conditionId: this.props.conditionId,
        excelVersion,
        columnFiledMap,
      })
      .then(res => {
        this.setState({ exporting: false });
        let b = new Blob([res.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        });
        FileSaver.saveAs(
          b,
          this.$t('budget.Budget.balance.query.result.export.table') +
            `.xls${excelVersion === '2003' ? '' : 'x'}`
        );
        hide();
        this.props.afterClose();
      })
      .catch(() => {
        this.setState({ exporting: false });
        message.error(this.$t({ id: 'importer.download.error.info' } /*下载失败，请重试*/));
        hide();
      });
  };

  handleRowClick = record => {
    let { selectedRowKeys } = this.state;
    let hasRecord = false;
    selectedRowKeys.map((key, index) => {
      if (key === record.dataIndex) {
        selectedRowKeys.splice(index, 1);
        hasRecord = true;
      }
    });
    !hasRecord && selectedRowKeys.push(record.dataIndex);
    this.setState({ selectedRowKeys });
  };

  onSelectItem = (record, selected) => {
    let { selectedRowKeys } = this.state;
    selected
      ? selectedRowKeys.push(record.dataIndex)
      : selectedRowKeys.map((key, index) => {
          if (key === record.dataIndex) {
            selectedRowKeys.splice(index, 1);
          }
        });
    this.setState({ selectedRowKeys });
  };

  //选择当页全部时的判断
  onSelectAll = (selected, selectedRows, changeRows) => {
    changeRows.map(changeRow => this.onSelectItem(changeRow, selected));
  };

  render() {
    const { visible, columns, dimensionColumns } = this.props;
    const { exporting, excelVersion, selectedRowKeys } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onSelect: this.onSelectItem,
      onSelectAll: this.onSelectAll,
    };
    return (
      <Modal
        visible={visible}
        width={800}
        destroyOnClose
        onCancel={this.props.onCancel}
        onOk={this.exportResult}
        afterClose={this.props.afterClose}
        title={this.$t('budget.select.column.export')}
        bodyStyle={{ height: '70vh', overflowY: 'scroll' }}
        confirmLoading={exporting}
        okText={this.$t('budget.export')}
        cancelText={this.$t({ id: 'common.cancel' })}
      >
        {this.$t('budget.export.is')}
        <RadioGroup
          onChange={e => this.setState({ excelVersion: e.target.value })}
          value={excelVersion}
        >
          <Radio value="2003">Excel 2003</Radio>
          <Radio value="2007">Excel 2007</Radio>
        </RadioGroup>

        <Table
          dataSource={columns.concat(dimensionColumns)}
          rowSelection={rowSelection}
          columns={[{ title: this.$t('budget.colum.name'), dataIndex: 'title' }]}
          rowKey="dataIndex"
          size="middle"
          onRow={record => ({
            onClick: () => this.handleRowClick(record),
          })}
          style={{ marginTop: 10, cursor: 'pointer' }}
          bordered
        />
      </Modal>
    );
  }
}

Exporter.propTypes = {
  visible: PropTypes.bool, //导入弹框是否可见
  onCancel: PropTypes.func, //点击取消回调
  onOk: PropTypes.func, //导入成功回调
  afterClose: PropTypes.func, //关闭后的回调
  conditionId: PropTypes.string, //预算查询sessionId
  columns: PropTypes.array, //需要导出的列
  dimensionColumns: PropTypes.array, //需要导出的维度列
};

Exporter.defaultProps = {
  onOk: () => {},
  afterClose: () => {},
  columns: [],
  dimensionColumns: [],
};

function mapStateToProps() {
  return {};
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(Exporter);
