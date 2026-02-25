Pod::Spec.new do |s|
  s.name = 'RNGainsightPx'
  s.version = '1.12.5'
  s.license = 'MIT'
  s.summary = 'GainsightPX Engine Swift'
  s.homepage = 'https://app.aptrinsic.com/authentication/login'
  s.authors = { 'GainsightPX Software Foundation' => 'pxsupport@gainsight.com' }
  s.source = { :git => ''}

  s.ios.deployment_target = '10.2'

  s.swift_version = '4.2'
  s.source_files = 'ios/*.{h,m}'
  s.dependency "React"
  s.dependency "Gainsight-PX", "1.13.0"
end