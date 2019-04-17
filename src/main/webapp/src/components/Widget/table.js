import { Table, ConfigProvider, Empty } from 'antd';
import { Resizable } from 'react-resizable';
import PropTypes from 'prop-types';

const ResizeableTitle = props => {
  const { onResize, width, ...restProps } = props;

  if (!width) {
    return <th {...restProps} />;
  }

  return (
    <Resizable width={width} height={0} onResize={onResize}>
      <th {...restProps} />
    </Resizable>
  );
};

class CustomTable extends React.Component {
  state = {
    columns: [],
    expandedRows: [],
  };

  componentDidMount() {
    this.setState({
      columns: this.props.columns,
    });
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      columns: nextProps.columns,
    });
  }

  components = {
    header: {
      cell: ResizeableTitle,
    },
  };

  handleResize = index => (e, { size }) => {
    this.setState(({ columns }) => {
      const nextColumns = [...columns];
      nextColumns[index] = {
        ...nextColumns[index],
        width: size.width,
      };
      return { columns: nextColumns };
    });
  };
  onExpandedRowsChange = keys => {
    if (this.props.onExpandedRowsChange) {
      this.props.onExpandedRowsChange(keys);
    } else {
      this.setState({ expandedRows: keys });
    }
  };
  render() {
    const columns =
      this.state.columns &&
      this.state.columns.map((col, index) => ({
        ...col,
        onHeaderCell: column => ({
          width: parseInt(column.width) || 120,
          onResize: this.handleResize(index),
        }),
      }));
    const { expandedRows } = this.state;

    return (
      <ConfigProvider renderEmpty={() => <Empty />}>
        <Table
          {...this.props}
          bordered
          components={this.components}
          columns={columns}
          expandedRowKeys={
            this.props.onExpandedRowsChange ? this.props.expandedRowKeys : expandedRows
          }
          onExpandedRowsChange={this.onExpandedRowsChange}
        />
      </ConfigProvider>
    );
  }
}

CustomTable.propTypes = {
  dataSource: PropTypes.array,
  columns: PropTypes.array,
  prefixCls: PropTypes.string,
  useFixedHeader: PropTypes.bool,
  rowSelection: PropTypes.object,
  className: PropTypes.string,
  size: PropTypes.string,
  loading: PropTypes.oneOfType([PropTypes.bool, PropTypes.object]),
  bordered: PropTypes.bool,
  onChange: PropTypes.func,
  locale: PropTypes.object,
  dropdownPrefixCls: PropTypes.string,
};

CustomTable.defaultProps = {
  dataSource: [],
  prefixCls: 'ant-table',
  useFixedHeader: false,
  className: '',
  size: 'default',
  loading: false,
  bordered: false,
  indentSize: 20,
  locale: {},
  rowKey: 'key',
  showHeader: true,
  defaultExpandAllRows: false,
  defaultExpandedRowKeys: [],
  expandedRowKeys: [],
};

export default CustomTable;
