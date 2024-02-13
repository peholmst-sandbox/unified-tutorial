import {createElement} from 'react';
import {createRoot} from 'react-dom/client';
import App from './App.js';
import {installAutoSignalTracking} from "@preact/signals-react/runtime";

// Without this, we can't use signals in our components
installAutoSignalTracking();

createRoot(document.getElementById('outlet')!).render(createElement(App));
