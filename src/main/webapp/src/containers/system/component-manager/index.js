import React from 'react';

import { Row, Col, Collapse, Card, Tree, Button, Drawer, Modal, Input } from 'antd';
const Panel = Collapse.Panel;
const TreeNode = Tree.TreeNode;

import { DragDropContext } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import ComponentItem from './component-item';
import ComponentContainer from './component-container';
import AttrForm from './attr-form';
import { routerRedux } from 'dva/router';
import ComponentList from './component-list';

import './index.less';

import { connect } from 'react-redux';

import New from './new';
import httpFetch from 'share/httpFetch';
// import MonacoEditor from 'react-monaco-editor';

class ComponentManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ref: null,
      timer: 0,
      nodes: [],
      componentsShow: false,
      saveShow: false,
      name: '',
      status: '',
      id: '',
      type: '',
      value: '50',
      version: {},
      previewVisible: false,
      result: '',
    };
  }

  callback = () => {};

  //复制
  copy = record => {
    configureStore.store.dispatch(
      replace({
        elements: JSON.parse(record.content),
      })
    );
    this.setState({ componentsShow: false, name: record.name, status: 'copy' });
  };

  //编辑
  edit = record => {
    const { dispatch } = this.props;

    dispatch({
      type: 'components/replace',
      payload: {
        components: JSON.parse(record.contents),
        version: { ...record },
      },
    });

    this.setState({ componentsShow: false });
  };

  onSelect = selectedKeys => {
    this.props.dispatch({
      type: 'components/selectedComponent',
      payload: selectedKeys[0],
    });
  };

  change = value => {
    let container = document.querySelector('.container');
    let scale = Number(value) / 100;
    let lastScale = 0;
    String(container.style.transform).replace(/scale\((.+)\)/g, function(value, key) {
      lastScale = Number(key);
    });
    container.style.transform = `scale(${scale})`;
    container.style.width = (container.clientWidth * lastScale) / (Number(value) / 100) + 'px';

    this.setState({ value });
  };

  componentWillUnmount() {
    window.clearInterval(this.state.timer);
  }

  handleKeyDown = e => {
    if ((e.key == 'z' || e.key == 'Z') && e.ctrlKey) {
      configureStore.store.dispatch(back());
    }
  };

  delete = () => {
    const { dispatch, selectedId } = this.props;

    dispatch({
      type: 'components/deleteComponent',
      payload: selectedId,
    });
  };

  priview = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/setting/priview',
      })
    );
  };

  back = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'components/back',
    });
  };

  //保存
  save = () => {
    this.setState({ saveShow: true });
  };

  handleCancel = () => {
    this.setState({ saveShow: false });
  };

  reset = () => {
    this.props.dispatch({
      type: 'components/reset',
    });
  };

  selectRoot = () => {
    const { dispatch } = this.props;

    dispatch({
      type: 'components/selectedComponent',
      payload: 0,
    });
  };

  renderNode = (id = 0) => {
    const { components } = this.props;

    let roots = components.filter(o => o.parent == id);

    return roots.map(item => {
      return (
        <TreeNode dateRef={item} title={item.type} key={item.id}>
          {this.renderNode(item.id)}
        </TreeNode>
      );
    });
  };

  onDragEnter = info => {
    // console.log(info);
    // expandedKeys 需要受控时设置
    // this.setState({
    //   expandedKeys: info.expandedKeys,
    // });
  };

  onDrop = info => {
    const { selectedId, components, version } = this.props;
    const dropKey = info.node.props.eventKey;
    const dragKey = info.dragNode.props.eventKey;
    const dropPos = info.node.props.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
    if (info.dropToGap) {
      let dragIndex = components.findIndex(o => o.id == dragKey);
      let columns = components.filter(o => o.parent == info.dragNode.props.dateRef.parent);
      let dropIndex = columns.findIndex(o => o.id == dropKey);
      let drag = components.splice(dragIndex, 1);

      if (dropPosition == -1) {
        components.splice(dropIndex, 0, drag[0]);
      } else {
        components.splice(dropIndex + 1, 0, drag[0]);
      }
    }
    this.props.dispatch({
      type: 'components/update',
      payload: { components },
    });
  };

  export = () => {
    const { components } = this.props;
    httpFetch.post('/utils/getFile', { components }).then(res => {
      this.setState({ previewVisible: true, result: res.data });
    });
  };

  render() {
    const { selectedId, components, version } = this.props;

    const { componentsShow, saveShow, previewVisible, result } = this.state;

    const text = version.id ? (version.status === 'copy' ? '复制组件' : '编辑组件') : '';

    return (
      <div>
        <Row className="component">
          <Col span={5}>
            <Card title="组件库" hoverable>
              <Collapse
                defaultExpandAll
                bordered={false}
                defaultActiveKey={['1', '2']}
                onChange={this.callback}
              >
                <Panel header="基础组件" key="1">
                  <Row gutter={20}>
                    <Col span={12}>
                      <ComponentItem params={{ isInline: true }} text="button" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="dropdown" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="row" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="col" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="slide-frame" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem params={{ isHeight: true }} text="card" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="table" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="table-column" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem params={{ isHeight: true }} text="content-layout" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem params={{ isHeight: true }} text="basic-info" />
                    </Col>
                  </Row>
                </Panel>
                <Panel header="表单组件" key="2">
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="search-form" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="form" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="input" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="select" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="switch" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="custom-chooser" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={12}>
                      <ComponentItem text="date-picker" />
                    </Col>
                    <Col span={12}>
                      <ComponentItem text="range-picker" />
                    </Col>
                  </Row>
                  <Row style={{ marginTop: 12 }} gutter={20}>
                    <Col span={24}>
                      <ComponentItem text="permissions-allocation" />
                    </Col>
                  </Row>
                </Panel>
              </Collapse>
            </Card>
          </Col>
          <Col span={12}>
            <Card onClick={this.selectRoot} title="页面" hoverable>
              <ComponentContainer />
            </Card>
          </Col>
          <Col className="attr" span={7}>
            <Card
              className="attr-box"
              title="页面布局"
              extra={<a onClick={this.delete}>删除</a>}
              hoverable
            >
              <Tree
                showLine
                selectedKeys={[String(selectedId)]}
                onSelect={this.onSelect}
                defaultExpandAll
                autoExpandParent
                expandedKeys={components.map(o => String(o.id))}
                draggable
                onDragEnter={this.onDragEnter}
                onDrop={this.onDrop}
              >
                {this.renderNode()}
              </Tree>
            </Card>
            <Card className="event-box" title="属性" hoverable>
              {!!selectedId && <AttrForm />}
            </Card>
          </Col>
        </Row>
        <div className="component-bottom-bar">
          <Button style={{ marginLeft: 40 }} onClick={this.priview} type="primary">
            预览
          </Button>
          <Button style={{ marginLeft: 20 }} onClick={this.back}>
            返回上一步
          </Button>
          <Button style={{ marginLeft: 20 }} onClick={this.save}>
            保存
          </Button>
          <Button style={{ marginLeft: 20 }} onClick={this.reset}>
            重置
          </Button>
          <Button style={{ marginLeft: 20 }} onClick={this.export}>
            导出
          </Button>
          <Button
            style={{ marginLeft: 20 }}
            onClick={() => this.setState({ componentsShow: true })}
          >
            页面列表
          </Button>
          {!!version.id && (
            <span style={{ marginLeft: 20 }}>
              {text}：<span style={{ color: '#333' }}>{version.componentName}</span>
            </span>
          )}
        </div>

        <Drawer
          title="页面列表"
          placement="right"
          destroyOnClose
          onClose={() => {
            this.setState({ componentsShow: false });
          }}
          visible={componentsShow}
          width="60vw"
        >
          <ComponentList onEdit={this.edit} />
        </Drawer>
        <New components={components} onClose={this.handleCancel} visible={saveShow} />
        {/* <Modal
          title="预览"
          visible={previewVisible}
          onOk={() => {
            this.setState({ previewVisible: false });
          }}
          onCancel={() => {
            this.setState({ previewVisible: false });
          }}
          bodyStyle={{ padding: 0 }}
          width="50%"
        >
          <MonacoEditor
            height="480"
            language="javascript"
            value={result}
            options={{
              selectOnLineNumbers: true,
              roundedSelection: false,
              readOnly: false,
              cursorStyle: 'line',
              automaticLayout: true,
            }}
          />
        </Modal> */}
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    components: state.components.components,
    selectedId: state.components.selectedId,
    version: state.components.version,
  };
}

export default connect(mapStateToProps)(DragDropContext(HTML5Backend)(ComponentManager));